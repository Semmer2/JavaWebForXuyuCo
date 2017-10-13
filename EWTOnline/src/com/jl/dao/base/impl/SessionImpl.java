package com.jl.dao.base.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.sql.DataSource;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CLOB;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.apache.axis.MessageContext;

import com.jl.util.Constants;
import com.jl.util.DateUtil;
import com.jl.util.RefUtil;
import com.jl.util.SqlType;
import com.jl.util.StringUtils;
import com.jl.util.UUID;

public class SessionImpl implements DBASession {
	private AppConnection myconn;

	private PreparedStatement stmt;

	private ResultSet rset;

	private boolean open;

	// private boolean longtran; 
	private boolean islongtransuccess;

	private Object innerObject;
	public HttpServletRequest getPagerequest() {
		return pagerequest;
	}

	private HttpServletRequest pagerequest;
	private static LoggerContext logger = new LoggerContext(SessionImpl.class);

	public SessionImpl(HttpServletRequest pagerequest) {
		open = false;
		myconn = null;
		islongtransuccess = true;
		this.pagerequest = pagerequest;
	}

	private boolean getLongtran() throws Exception {

		Object tranconnectionid = Context.getInstance().get("tranconnectionid");
		if (tranconnectionid == null)
			tranconnectionid = "";
		if (tranconnectionid.equals("") == false)
			return true;
		else
			return false;
	}

	private String nullSession(String methodname) {
		logger.error("session is null when run method " + methodname);
		return "";
	}

	public int getTransuccess() throws Exception {

		if (getLongtran()) {
			return (islongtransuccess == true ? 1 : 0);
		} else {
			return -1;
		}
	}

	public Object startlongTran() throws Exception {
		if (!getLongtran()) {
			if (open || myconn != null)
				close();
			getMYConnection();
			innerObject = new Object();
			islongtransuccess = true;
			innerObject = myconn.getId();
			Context.getInstance().put("tranconnectionid",innerObject.toString());
			ConnectionMap.add(myconn, innerObject.toString());
			return innerObject;
		} else {
			return null;
		}
	}

	
	public void setlongTranFail(Object tranObject) {
		try {
			if (getLongtran()) {
				if (tranObject != null && islongtransuccess) {
					islongtransuccess = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}
	}

	public boolean endlongTran(Object tranObject) throws Exception {
		boolean rtn = false;
		try {
			if (tranObject != null && islongtransuccess) {
				if (tranObject.toString().equalsIgnoreCase(myconn.getId())) {
					myconn.getConnection().commit();
					myconn.getConnection().setAutoCommit(true);
					rtn = true;
				} else {
					rtn = false;
				}

			} else {
				if (tranObject != null) {
					if (tranObject.toString().equalsIgnoreCase(myconn.getId())) {
						myconn.getConnection().rollback();
						Context.getInstance().put("tranconnectionid", "");
						ConnectionMap.remove(tranObject.toString());
					}
				}
			}

		} catch (SQLException e) {
			if (tranObject != null) {
				if (tranObject.toString().equalsIgnoreCase(myconn.getId())) {
					myconn.getConnection().rollback();
					Context.getInstance().put("tranconnectionid", "");
					ConnectionMap.remove(tranObject.toString());
				}
			}
			logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (tranObject != null) {
					islongtransuccess = true;

					Context.getInstance().put("tranconnectionid", "");
					ConnectionMap.remove(tranObject.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("endlongTran error");
				logger.error(e.getMessage());
			} finally {
				if (tranObject != null)
					close();
			}
		}
		return rtn;
	}

	public Connection getConnection() throws Exception {
		String datasourcetype = Constants.getValue("datasourcetype");
		if (datasourcetype.equalsIgnoreCase("jdbc")) {
			return getConnectionByJdbc();
		} else {
			if (datasourcetype.equalsIgnoreCase("jndi")) {
				return getConnectionByJndi();
			} else {
				return null;
			}
		}
	}

	private Connection getConnectionByJdbc() throws Exception {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String jdbcinfo = Constants.getValue("datajdbcurl");
			String jdbc[] = jdbcinfo.split(",");
			String url = jdbc[0];
			String username = jdbc[1];
			String password = jdbc[2];
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Connection getConnectionByJndi() throws SQLException {
		javax.naming.Context ctx = null;
		Hashtable ht = new Hashtable();
		try {
			String jndiinfo = Constants.getValue("weblogicjndiurl");
			String[] jndi = jndiinfo.split(",");
			String ServerConfig = jndi[0];
			String jndiname = jndi[1];
			ht.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
					"weblogic.jndi.WLInitialContextFactory");
			ht.put(javax.naming.Context.PROVIDER_URL, ServerConfig);
			//ht.put(javax.naming.Context.SECURITY_PRINCIPAL, "weblogic");
			//ht.put(javax.naming.Context.SECURITY_CREDENTIALS, "weblogic");
			ctx = new InitialContext(ht);
			//ctx = (javax.naming.Context) new InitialContext().lookup("java:comp/env");
			DataSource ds = null;
			ds = (DataSource) ctx.lookup(jndiname);
			Connection dbconn = ds.getConnection();
			return dbconn;
		} catch (NamingException e1) {
			System.out.println(e1.toString());
			System.out.println("�����û���ҵ���");
			return null;
		} catch (SQLException e2) {
			System.out.println(e2.toString());
			System.out.println("��ݿ��쳣��");
			return null;
		}
	}

	public AppConnection getMYConnection() throws Exception {
		Connection dbcon = null;
		try {
			if (myconn == null) {
				if(innerObject==null)
				{
					open = true;
					dbcon = this.getConnection();
					dbcon.setAutoCommit(false);
					String connectionid = UUID.randomUUID().toString();
					myconn = new AppConnection(dbcon, connectionid);
					ConnectionMap.add(myconn, connectionid);
				}
				else
				{
					Object tranconnectionid = Context.getInstance().get("tranconnectionid");
					myconn =((AppConnection) ConnectionMap.get(tranconnectionid.toString()));
				}	
				return myconn;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
		return myconn;
	}

	public List getColNames(String sql) throws Exception {
		List colnmnList = new ArrayList();
		ResultSet rs = this.openSelectbyRS(sql);
		try {
			ResultSetMetaData rm = null;
			rm = rs.getMetaData();
			int cnt = rm.getColumnCount();
			for (int i = 1; i <= cnt; i++) {
				String column[] = new String[2];
				column[0] = rm.getColumnName(i);
				column[1] = rm.getColumnTypeName(i);
				colnmnList.add(column);
			}
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			close();
		}
		return colnmnList;
	}

	private List ConvertResultSetToTreeList(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = null;
		List rows = new ArrayList();
		if (rs != null) {
			rsmd = rs.getMetaData();
			// rs.beforeFirst();
			while (rs.next()) {
				TreeMap rowhashtable = new TreeMap();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String columnName = rsmd.getColumnName(i + 1).toLowerCase();
					switch (rsmd.getColumnType(i + 1)) {
					case Types.NUMERIC:
						if (rs.getString(columnName) == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, rs
									.getString(columnName));
						break;
					case Types.VARCHAR:
						rowhashtable.put(columnName,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName));
						break;
					case Types.INTEGER:
						rowhashtable.put(columnName,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName));
						break;
					case Types.DATE: {
						java.sql.Date date = rs.getDate(columnName);
						if (date == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable
									.put(columnName, rs.getDate(columnName));
						break;
					}
					case Types.TIMESTAMP: {
						java.sql.Timestamp timestamp = rs
								.getTimestamp(columnName);
						if (timestamp == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, rs
									.getTimestamp(columnName));
						break;
					}
					case Types.BLOB: {
						//oracle.sql.BLOB blobdata =(oracle.sql.BLOB)rs.getBlob(columnName);
						Blob blobdata = rs.getBlob(columnName);
						if (blobdata == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, StringUtils
									.getStringFromBlob(blobdata));
						break;
					}
					default: {
						rowhashtable.put(columnName,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName));
						break;
					}
					}
				}
				rows.add((Object) rowhashtable);
			}
		}
		return rows;
	}

	private List ConvertResultSetToSortList(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = null;
		List rows = new ArrayList();
		if (rs != null) {
			rsmd = rs.getMetaData();
			int cnt = rsmd.getColumnCount();
			// rs.beforeFirst();
			while (rs.next()) {
				Object[] rowhashtable = new Object[cnt];
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String columnName = rsmd.getColumnName(i + 1).toLowerCase();
					switch (rsmd.getColumnType(i + 1)) {
					case Types.NUMERIC:
						if (rs.getString(columnName) == null)
							rowhashtable[i] = "";
						else
							rowhashtable[i] = rs.getString(columnName);
						break;
					case Types.VARCHAR:
						rowhashtable[i] = rs.getString(columnName) == null ? ""
								: rs.getString(columnName);
						break;
					case Types.INTEGER:
						rowhashtable[i] = rs.getString(columnName) == null ? ""
								: rs.getString(columnName);
						break;
					case Types.DATE: {
						java.sql.Date date = rs.getDate(columnName);
						if (date == null)
							rowhashtable[i] = "";
						else
							rowhashtable[i] = rs.getDate(columnName);
						break;
					}
					case Types.TIMESTAMP: {
						java.sql.Timestamp timestamp = rs
								.getTimestamp(columnName);
						if (timestamp == null)
							rowhashtable[i] = "";
						else
							rowhashtable[i] = rs.getTimestamp(columnName);
						break;
					}
					case Types.BLOB: {
						//oracle.sql.BLOB blobdata =(oracle.sql.BLOB)rs.getBlob(columnName);
						Blob blobdata = rs.getBlob(columnName);
						if (blobdata == null)
							rowhashtable[i] = "";
						else
							rowhashtable[i] = StringUtils
									.getStringFromBlob(blobdata);
						break;
					}
					default: {
						rowhashtable[i] = rs.getString(columnName) == null ? ""
								: rs.getString(columnName);
						break;
					}
					}
				}
				rows.add((Object) rowhashtable);
			}
		}
		return rows;
	}

	private String[] getDBColNames(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = null;
		String[] rows = null;
		if (rs != null) {
			rsmd = rs.getMetaData();
			int cnt = rsmd.getColumnCount();
			rows = new String[cnt];
			for (int i = 0; i < cnt; i++) {
				String columnName = rsmd.getColumnName(i + 1).toLowerCase();
				rows[i] = columnName;
				// logger.info(i+"---"+columnName);
			}
		}
		return rows;
	}

	private List ConvertResultSetToList(ResultSet rs) throws SQLException,
			IOException {
		ResultSetMetaData rsmd = null;
		List rows = new ArrayList();
		if (rs != null) {
			rsmd = rs.getMetaData();
			// rs.beforeFirst();
			while (rs.next()) {
				Hashtable rowhashtable = new Hashtable();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String columnName = rsmd.getColumnName(i + 1).toLowerCase();
					switch (rsmd.getColumnType(i + 1)) {
					case Types.NUMERIC:
						if (rs.getString(columnName) == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, rs
									.getString(columnName).trim());
						break;
					case Types.VARCHAR:
						rowhashtable.put(columnName,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName).trim());
						break;
					case Types.INTEGER:
						rowhashtable.put(columnName,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName).trim());
						break;
					case Types.DATE: {
						java.sql.Date date = rs.getDate(columnName);
						if (date == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable
									.put(columnName, rs.getDate(columnName));
						break;
					}
					case Types.TIMESTAMP: {
						java.sql.Timestamp timestamp = rs
								.getTimestamp(columnName);
						if (timestamp == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, rs
									.getTimestamp(columnName));
						break;
					}
					case Types.BLOB: {
						//oracle.sql.BLOB blobdata =(oracle.sql.BLOB)rs.getBlob(columnName);
						Blob blobdata = rs.getBlob(columnName);
						if (blobdata == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, StringUtils
									.getStringFromBlob(blobdata).trim());
						break;
					}
					case Types.CLOB: {
						Clob blobdata = rs.getClob(columnName);
						if (blobdata == null)
							rowhashtable.put(columnName, "");
						else
							rowhashtable.put(columnName, RefUtil
									.getStringFromClob((CLOB) blobdata).trim());
						break;
					}
					case Types.LONGVARCHAR: {
						java.io.Reader long_out = rs
								.getCharacterStream(columnName);
						if (long_out != null) {
							char[] long_buf = new char[8192];
							StringBuffer buffer = new StringBuffer();
							int len = 0;
							try {
								while ((len = long_out.read(long_buf)) > 0) {
									buffer.append(long_buf, 0, len);
								}
								rowhashtable.put(columnName, buffer.toString().trim());
								buffer = null;
							} catch (IOException e) {
								logger.error(e.getMessage());
								e.printStackTrace();
								throw e;
							} finally {
								long_buf = null;
							}
						}
						break;
					}
					default: {
						rowhashtable.put(columnName,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName).trim());
						break;
					}
					}
				}
				rows.add((Object) rowhashtable);
			}
		}
		return rows;
	}

	public List openSelectbyTreeList(String s) throws Exception {
		logger.info("openSelectbyTreeList:" + s);
		List ls = null;
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) 
			{
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			try {
				stmt = myconn.getConnection().prepareStatement(s);
				if (stmt == null)
					throw new Exception("stmt is null");
				try {
					rset = stmt.executeQuery();
					if (rset == null)
						throw new Exception("ResultSet is null");
					ls = ConvertResultSetToTreeList(rset);
					return ls;
				} finally {
					if (rset != null) {
						rset.close();
						rset = null;
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				close();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			logger.error(s);
			ex.printStackTrace();
			throw ex;
		}
	}

	public List openSelectbySortList(String s) throws Exception {
		logger.info("openSelectbySortList:" + s);
		List ls = null;
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) // û�д�����
			{
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			try {
				stmt = myconn.getConnection().prepareStatement(s);
				if (stmt == null)
					throw new Exception("stmt is null");
				try {
					rset = stmt.executeQuery();
					if (rset == null)
						throw new Exception("ResultSet is null");
					ls = ConvertResultSetToSortList(rset);
					return ls;
				} finally {
					if (rset != null) {
						rset.close();
						rset = null;
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				close();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			logger.error(s);
			ex.printStackTrace();
			throw ex;
		}
	}

	public String[] getDBColNames(String s) throws Exception {
		logger.info("getDBColNames:" + s);
		String[] ls = null;
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) // û�д�����
			{
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			try {
				String[] sqlarr = s.toLowerCase().split("where");
				s = sqlarr[0] + " where 1>2";
				stmt = myconn.getConnection().prepareStatement(s);
				if (stmt == null)
					throw new Exception("stmt is null");
				try {
					rset = stmt.executeQuery();
					if (rset == null)
						throw new Exception("ResultSet is null");
					ls = this.getDBColNames(rset);
				} finally {
					if (rset != null) {
						rset.close();
						rset = null;
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				close();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			logger.error(s);
			ex.printStackTrace();
			throw ex;
		}
		return ls;
	}

	public List openSelectbyList(String s) throws Exception {
		logger.info("openSelectbyList:" + s);
		List ls = null;
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) // û�д�����
			{
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			try {
				stmt = myconn.getConnection().prepareStatement(s);
				if (stmt == null)
					throw new Exception("stmt is null");
				try {
					rset = stmt.executeQuery();
					if (rset == null)
						throw new Exception("ResultSet is null");
					ls = ConvertResultSetToList(rset);
					return ls;
				} finally {
					if (rset != null) {
						rset.close();
						rset = null;
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				close();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			logger.error(s);
			ex.printStackTrace();
			throw ex;
		}
	}

	public List openSelectbyList(String s, Object[] parameters)
			throws Exception {
		List ls = null;
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) { // û�д�����
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			try {
				String pagesize="";
				String currentpage="";
				if(pagerequest.getAttribute("pagesize")!=null)
				{
					pagesize=pagerequest.getAttribute("pagesize").toString();
					currentpage="1";
				}
				if(pagerequest.getParameter("currpage") != null)
				{
					currentpage=pagerequest.getParameter("currpage").toString();
				}
				String DBRecordNum=this.pagerequest.getParameter("DBRecordNum");
				if(pagesize.equalsIgnoreCase("")==false && DBRecordNum==null && pagerequest.getAttribute("totalresultcount")==null)
				{
					String totalsql="select count(1) as totalresultcount from ("+s+")";	
					stmt = myconn.getConnection().prepareStatement(totalsql);
					if (stmt == null)
						throw new Exception("stmt is null");
					if (parameters != null) {
						for (int i = 0, j = 0; j < parameters.length; j++) {
							if (parameters[j] == null) {
								// stmt.setString(i + 1, "");
								continue;
							} else {
								Class parameterTypeClass = parameters[j].getClass();
								String parameterTypeName = parameterTypeClass
										.getName();
								if (parameterTypeName.equals("java.lang.Integer")
										|| parameterTypeName.equals("int")) {
									stmt.setInt(i + 1, ((Integer) parameters[j])
											.intValue());
								} else if (parameterTypeName
										.equals("java.lang.Double")
										|| parameterTypeName.equals("double")) {
									stmt.setDouble(i + 1, ((Double) parameters[j])
											.doubleValue());
								} else {
									if (parameterTypeName
											.equalsIgnoreCase("java.lang.Float")) {
										stmt.setFloat(i + 1,
												((Float) parameters[j])
														.floatValue());
									} else {
										if (parameterTypeName
												.equals("java.lang.String")) {

											String tmpvalue = "";
											if (parameters[j] != null) {
												tmpvalue = com.jl.util.StringUtils
														.replaceString(
																parameters[j]
																		.toString(),
																"'", "\"");
											}
											stmt.setString(i + 1, tmpvalue);

										} else {
											if (parameterTypeName
													.equals("java.sql.Timestamp")) {
												stmt
														.setTimestamp(
																i + 1,
																parameters[j] == null ? null
																		: (java.sql.Timestamp) parameters[j]);
											} else {
												if (parameterTypeName
														.equals("java.sql.Date")) {
													stmt
															.setDate(
																	i + 1,
																	parameters[j] == null ? null
																			: (java.sql.Date) parameters[j]);
												} else {
													if (parameterTypeName
															.equals("java.lang.Long")
															|| parameterTypeName
																	.equals("long")) {
														stmt
																.setLong(
																		i + 1,
																		((Long) parameters[j])
																				.longValue());
													}
												}
											}
										}
									}
								}
								i++;
							}
						}
					}
					try {
						rset = stmt.executeQuery();
						if (rset == null)
							throw new Exception("ResultSet is null");
						ls = ConvertResultSetToList(rset);
						if(ls.size()==1)
						{
							Hashtable ht=(Hashtable)ls.get(0);
							String totalresultcount=ht.get("totalresultcount").toString();
							this.pagerequest.setAttribute("totalresultcount",totalresultcount);
						}						
					} finally {
						if (rset != null) {
							rset.close();
							rset = null;
						}
					}
				}
				else
				{
					this.pagerequest.setAttribute("totalresultcount",DBRecordNum);
				}
				if(pagesize.equalsIgnoreCase("")==false && currentpage.equalsIgnoreCase("")==false)
				{
					//s="select * from (select rownum as rownumvalue,datatablename.* from ("+s+") datatablename) where rownumvalue>=("+currentpage+"-1)*"+pagesize+"+1 and rownumvalue<="+currentpage+"*"+pagesize;
					s="SELECT * FROM ("+s+") t WHERE pagerownum>=("+currentpage+"-1)*"+pagesize+"+1 and pagerownum<="+currentpage+"*"+pagesize;
					
				}
				stmt = myconn.getConnection().prepareStatement(s);
				if (stmt == null)
					throw new Exception("stmt is null");
				if (parameters != null) {
					for (int i = 0, j = 0; j < parameters.length; j++) {
						if (parameters[j] == null) {
							// stmt.setString(i + 1, "");
							continue;
						} else {
							Class parameterTypeClass = parameters[j].getClass();
							String parameterTypeName = parameterTypeClass
									.getName();
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								stmt.setInt(i + 1, ((Integer) parameters[j])
										.intValue());
							} else if (parameterTypeName
									.equals("java.lang.Double")
									|| parameterTypeName.equals("double")) {
								stmt.setDouble(i + 1, ((Double) parameters[j])
										.doubleValue());
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									stmt.setFloat(i + 1,
											((Float) parameters[j])
													.floatValue());
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {

										String tmpvalue = "";
										if (parameters[j] != null) {
											tmpvalue = com.jl.util.StringUtils
													.replaceString(
															parameters[j]
																	.toString(),
															"'", "\"");
										}
										stmt.setString(i + 1, tmpvalue);

									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											stmt
													.setTimestamp(
															i + 1,
															parameters[j] == null ? null
																	: (java.sql.Timestamp) parameters[j]);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												stmt
														.setDate(
																i + 1,
																parameters[j] == null ? null
																		: (java.sql.Date) parameters[j]);
											} else {
												if (parameterTypeName
														.equals("java.lang.Long")
														|| parameterTypeName
																.equals("long")) {
													stmt
															.setLong(
																	i + 1,
																	((Long) parameters[j])
																			.longValue());
												}
											}
										}
									}
								}
							}
							i++;
						}
					}
				}
				try {
					rset = stmt.executeQuery();
					if (rset == null)
						throw new Exception("ResultSet is null");
					ls = ConvertResultSetToList(rset);
					return ls;
				} finally {
					if (rset != null) {
						rset.close();
						rset = null;
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				close();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
	}

	private ResultSet openSelectbyRS(String s) throws Exception {
		logger.info("openSelectbyRS:" + s);
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		if (!open) // û�д�����
		{
			if (myconn == null) {
				myconn = getMYConnection();
				myconn.getConnection().setAutoCommit(false);
			}
			open = true;
		}
		if (myconn == null)
			throw new Exception("��openSelectʱconn����ΪNULL");
		try {
			stmt = myconn.getConnection().prepareStatement(s,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (stmt == null)
				throw new Exception("stmt is null");
			rset = stmt.executeQuery();
			if (rset == null)
				throw new Exception("ResultSet is null");
			open = true;
			return rset;
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			logger.error(s);
			ex.printStackTrace();
			throw ex;
		}
	}

	// �õ�����ֵ
	public BlobObject getBlobObject(String s, Object[] parameters,
			String blobfieldname) throws Exception {
		List ls = null;
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) { // û�д�����
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			stmt = myconn.getConnection().prepareStatement(s);
			if (stmt == null)
				throw new Exception("stmt is null");
			if (parameters != null) {
				for (int i = 0, j = 0; j < parameters.length; j++) {
					if (parameters[j] == null) {
						// stmt.setString(i + 1, "");
						continue;
					} else {
						Class parameterTypeClass = parameters[j].getClass();
						String parameterTypeName = parameterTypeClass.getName();
						if (parameterTypeName.equals("java.lang.Integer")
								|| parameterTypeName.equals("int")) {
							stmt.setInt(i + 1, ((Integer) parameters[j])
									.intValue());
						} else if (parameterTypeName.equals("java.lang.Double")
								|| parameterTypeName.equals("double")) {
							stmt.setDouble(i + 1, ((Double) parameters[j])
									.doubleValue());
						} else {
							if (parameterTypeName
									.equalsIgnoreCase("java.lang.Float")) {
								stmt.setFloat(i + 1, ((Float) parameters[j])
										.floatValue());
							} else {
								if (parameterTypeName
										.equals("java.lang.String")) {

									String tmpvalue = "";
									if (parameters[j] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[j]
														.toString(), "'", "\"");
									}
									stmt.setString(i + 1, tmpvalue);

								} else {
									if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										stmt
												.setTimestamp(
														i + 1,
														parameters[j] == null ? null
																: (java.sql.Timestamp) parameters[j]);
									} else {
										if (parameterTypeName
												.equals("java.sql.Date")) {
											stmt
													.setDate(
															i + 1,
															parameters[j] == null ? null
																	: (java.sql.Date) parameters[j]);
										} else {
											if (parameterTypeName
													.equals("java.lang.Long")
													|| parameterTypeName
															.equals("long")) {
												stmt.setLong(i + 1,
														((Long) parameters[j])
																.longValue());
											}
										}
									}
								}
							}
						}
						i++;
					}
				}
			}
			rset = stmt.executeQuery();
			if (rset == null)
				throw new Exception("ResultSet is null");
			rset.next();
			oracle.sql.BLOB blobdata = (oracle.sql.BLOB) rset
					.getBlob(blobfieldname);
			if (blobdata != null) {
				BlobObject blobobject = new BlobObject(rset, stmt, myconn,
						blobdata);
				return blobobject;
			}

		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			close();
			throw ex;
		}
		return null;
	}

	// �õ����ֶ�ֵ
	public Object openSelectByObject(String fieldName, String sql)
			throws Exception {
		List ls = this.openSelectbyList(sql);
		if (ls.size() > 1) {
			throw new Exception("���ֵ������1");
		} else if (ls.size() > 0) {
			Hashtable ht = (Hashtable) ls.get(0);
			return ht.get(fieldName);
		}
		return null;
	}

	// �õ����ֶ�ֵ
	public Object openSelectByObject(String fieldName, String sql, Object[] p)
			throws Exception {
		List ls = this.openSelectbyList(sql, p);
		if (ls.size() > 1) {
			throw new Exception("���ֵ������1");
		} else if (ls.size() > 0) {
			Hashtable ht = (Hashtable) ls.get(0);
			return ht.get(fieldName);
		}
		return null;
	}

	public int getRecordNum(String strSql) throws SQLException {
		int recordNum = 0;
		try {
			int intPos = 0;
			String strRet = null;
			if (strSql != null) {
				intPos = strSql.indexOf(" FROM ");
				if (intPos == -1) {
					intPos = strSql.indexOf(" from ");
				}
				if (intPos == -1) {
					intPos = strSql.indexOf("from");
				}
				if (intPos == -1) {
					intPos = strSql.toLowerCase().indexOf("from");
				}
				if (intPos != -1) {
					strRet = "SELECT COUNT(*) as resultcount "
							+ strSql.substring(intPos);
				}
				List ls = this.openSelectbyList(strRet);
				if (ls.size() > 0) {
					Hashtable ht = (Hashtable) ls.get(0);
					recordNum = (new Integer(ht.get("resultcount").toString()))
							.intValue();
				}
				ls = null;
				close();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.error(strSql);
			ex.printStackTrace();
			throw new SQLException(ex.getMessage());
		}
		return recordNum;
	}

	public boolean runSql(String[] dmlSql, Object[][] parameters)
			throws Exception {
		logger.info("runSql:" + dmlSql);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("����һ������ִ�У���ݿ����Ӷ����ڿ�ʼ����֮ǰ�԰Ѵ���!");
		if (dmlSql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		PreparedStatement prepareStatement = null;
		oracle.sql.BLOB ioblob = null;
		oracle.sql.BLOB blob = null;
		try {
			if (!open && !getLongtran()) {

				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				} else {
					// !getLongtran() && open already throws Exception
				}
			}
			if (open && getLongtran()){
				myconn.getConnection().setAutoCommit(false);
			}
			try {
				for (int j = 0; j < dmlSql.length; j++) {
					if (!dmlSql[j].equalsIgnoreCase("")) {
						prepareStatement = myconn.getConnection()
								.prepareStatement(dmlSql[j]);
						if (prepareStatement == null)
							throw new Exception(
									"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
						if (parameters != null) {
							for (int i = 0; i < parameters[j].length; i++) {
								if (parameters[j][i] != null) {
									Class parameterTypeClass = parameters[j][i]
											.getClass();
									String parameterTypeName = parameterTypeClass
											.getName();
									if (parameterTypeName
											.equals("java.lang.Integer")
											|| parameterTypeName.equals("int")) {
										prepareStatement.setInt(i + 1,
												((Integer) parameters[j][i])
														.intValue());
									} else {
										if (parameterTypeName
												.equalsIgnoreCase("java.lang.Float")) {
											prepareStatement.setFloat(i + 1,
													((Float) parameters[j][i])
															.floatValue());
										} else {
											if (parameterTypeName
													.equals("java.lang.String")) {
												String tmpvalue = "";
												if (parameters[j][i] != null) {
													tmpvalue = com.jl.util.StringUtils
															.replaceString(
																	parameters[j][i]
																			.toString(),
																	"'", "\"");
												}
												prepareStatement.setString(
														i + 1, tmpvalue);

											} else {
												if (parameterTypeName
														.equals("java.sql.Timestamp")) {
													prepareStatement
															.setTimestamp(
																	i + 1,
																	(java.sql.Timestamp) parameters[j][i]);
												} else {
													if (parameterTypeName
															.equals("java.sql.Date")) {
														if (parameters[j][i] != null) {
															prepareStatement
																	.setDate(
																			i + 1,
																			(java.sql.Date) parameters[j][i]);
														} else {
															prepareStatement
																	.setDate(
																			i + 1,
																			null);
														}
													} else {
														if (parameterTypeName
																.equals("java.lang.Long")
																|| parameterTypeName
																		.equals("long")) {
															stmt
																	.setLong(
																			i + 1,
																			((Long) parameters[j][i])
																					.longValue());
														} else {
															if (parameterTypeName
																	.equals("java.lang.Long")
																	|| parameterTypeName
																			.equals("long")) {
																stmt
																		.setLong(
																				i + 1,
																				((Long) parameters[j][i])
																						.longValue());
															}
															if (parameterTypeName
																	.equals("java.io.File")) {
																// ������Ӧ���ļ���
																ioblob = convertFiletoBlob((File) parameters[j][i]);
																stmt
																		.setObject(
																				i,
																				ioblob,
																				Types.BLOB);
															}
															if (parameterTypeName
																	.equals("java.io.ByteArrayInputStream")) {
																blob = convertStringtoBlob((ByteArrayInputStream) parameters[j][i]);
																stmt
																		.setObject(
																				i,
																				((oracle.sql.BLOB) blob),
																				Types.BLOB);
															}
														}
													}
												}
											}
										}
									}
								} else {
									prepareStatement.setObject(i + 1, null);
								}
							}
						}
						prepareStatement.executeUpdate();
					}
				}
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				return true;
			} finally {
				if (prepareStatement != null) {
					prepareStatement.close();
					prepareStatement = null;
				}
				if (blob != null) {
					oracle.sql.BLOB.freeTemporary(blob);
				}
				if (ioblob != null) {
					oracle.sql.BLOB.freeTemporary(ioblob);
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw exception;
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					/*
					 * TransactionScope.end(obj);
					 * ServletSessionManager.endStandAloneSession();
					 */
					close();

				}
			}
		}
	}

	public boolean runSql(String[] dmlSql) throws Exception {
		logger.info("runSql:" + dmlSql);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("����һ������ִ�У���ݿ����Ӷ����ڿ�ʼ����֮ǰ�԰Ѵ���!");
		if (dmlSql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		PreparedStatement prepareStatement = null;
		try {
			if (!open && !getLongtran()) {
				/*
				 * ServletSessionManager.startStandAloneSession("JTF", true,
				 * "SYSADMIN", "SYSADMIN"); TransactionScope.begin(obj);
				 */
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				} else {
					// !getLongtran() && open already throws Exception
				}
			}
			if (open && getLongtran())
			{
				myconn.getConnection().setAutoCommit(false);
			}
			try {
				for (int j = 0; j < dmlSql.length; j++) {
					if (!dmlSql[j].equalsIgnoreCase("")) {
						prepareStatement = myconn.getConnection()
								.prepareStatement(dmlSql[j]);
						if (prepareStatement == null)
							throw new Exception(
									"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
						prepareStatement.executeUpdate();
					}
				}
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				return true;
			} finally {
				if (prepareStatement != null) {
					prepareStatement.close();
					prepareStatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					/*
					 * TransactionScope.end(obj);
					 * ServletSessionManager.endStandAloneSession();
					 */
					close();
				}
			}
		}
	}

	private oracle.sql.BLOB convertFiletoBlob(File file) throws Exception {
		InputStream is = new java.io.FileInputStream(file);
		oracle.sql.BLOB ioblob = oracle.sql.BLOB.createTemporary(myconn
				.getConnection(), true, 1);
		OutputStream outStream = ioblob.getBinaryOutputStream();
		// ��������д�������
		byte[] b = new byte[ioblob.getBufferSize()];
		int len = 0;
		int start = 0;
		while ((len = is.read(b)) != -1) {
			// b=new String(b).getBytes("iso-8859-1");
			outStream.write(b, 0, len);
			start = start + len + 1;
		}
		outStream.flush();
		outStream.close();

		is.close();
		return ioblob;
	}

	private oracle.sql.BLOB convertStringtoBlob(InputStream is)
			throws Exception {
		oracle.sql.BLOB ioblob = oracle.sql.BLOB.createTemporary(myconn
				.getConnection(), true, 1);
		OutputStream outStream = ioblob.getBinaryOutputStream();
		// ��������д�������
		byte[] b = new byte[ioblob.getBufferSize()];
		int len = 0;
		int start = 0;
		while ((len = is.read(b)) != -1) {
			outStream.write(b, 0, len);
			start = start + len + 1;
		}
		outStream.flush();
		outStream.close();

		is.close();
		return ioblob;
	}

	public boolean runSql(String sql, String[] param, Object bean)
			throws Exception, SQLException {
		logger.info("runSql:" + sql);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (sql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		if (sql.trim().equals(""))
			throw new Exception("��ִ��runSqlʱִ����䲻��Ϊ��!");
		PreparedStatement prepareStatement = null;
		java.io.File file = null;
		InputStream is = null;
		oracle.sql.BLOB ioblob = null;
		oracle.sql.BLOB blob = null;

		boolean result = false;
		Object[] values = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				}
			}
			if (open && getLongtran())
				myconn.getConnection().setAutoCommit(false);
			try {
				prepareStatement = myconn.getConnection().prepareStatement(sql);
				if (prepareStatement == null)
					throw new Exception(
							"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
				if (param != null) {
					values = new Object[param.length];
					Class beanClass = bean.getClass();
					for (int i = 1; i <= param.length; i++) {
						String fieldName = param[i - 1];
						String compareMethodName = "get" + fieldName;
						String beanMethodName = RefUtil.getMethodName(
								beanClass, compareMethodName);
						if (!beanMethodName.equals("")) {
							Method m = null;
							try {
								m = beanClass.getMethod(beanMethodName, null);
							} catch (NoSuchMethodException e) {
								logger.error(e.getMessage());
								e.printStackTrace();
								continue;
							}
							if (m != null) {
								try {
									// ȡ�ֶ�����
									Object rtn = m.invoke(bean, null);
									if (rtn != null) {
										// logger.info(fieldName +":" +
										// rtn.toString());
										String parameterTypeName = rtn
												.getClass().getName();
										if (parameterTypeName
												.equals("java.lang.Integer")
												|| parameterTypeName
														.equals("int")) {
											prepareStatement.setInt(i,
													new Integer(rtn.toString())
															.intValue());
											values[i - 1] = rtn.toString();
										}
										if (parameterTypeName
												.equals("java.lang.Float")
												|| parameterTypeName
														.equals("float")) {
											prepareStatement
													.setFloat(
															i,
															Float
																	.parseFloat(rtn
																			.toString()));
											values[i - 1] = rtn.toString();
										}
										if (parameterTypeName
												.equals("java.lang.Double")
												|| parameterTypeName
														.equals("double")) {
											prepareStatement.setDouble(i,
													Double.parseDouble(rtn
															.toString()));
											values[i - 1] = rtn.toString();
										}
										if (parameterTypeName
												.equals("java.lang.Long")
												|| parameterTypeName
														.equals("long")) {
											prepareStatement.setLong(i,
													new Long(rtn.toString())
															.longValue());
											values[i - 1] = rtn.toString();
										}
										if (parameterTypeName.equals("int")) {
											prepareStatement.setInt(i,
													new Integer(rtn.toString())
															.intValue());
											values[i - 1] = rtn.toString();
										}
										if (parameterTypeName.equals("float")) {
											prepareStatement
													.setFloat(
															i,
															Float
																	.parseFloat(rtn
																			.toString()));
											values[i - 1] = rtn.toString();
										}
										if (parameterTypeName
												.equals("java.lang.String")) {
											String tmpvalue = "";
											if (rtn != null) {
												tmpvalue = com.jl.util.StringUtils
														.replaceString(rtn
																.toString(),
																"'", "\"");
											}
											prepareStatement.setString(i,
													tmpvalue);
											values[i - 1] = tmpvalue;
										}
										if (parameterTypeName
												.equals("java.util.Date")) {
											java.util.Date utilDate;
											utilDate = (java.util.Date) rtn;
											java.sql.Timestamp sqlDate = new java.sql.Timestamp(
													utilDate.getTime());
											prepareStatement.setTimestamp(i,
													sqlDate);
											values[i - 1] = sqlDate;
										} else if (parameterTypeName
												.equals("java.sql.Date")) {
											java.sql.Date d = (java.sql.Date) rtn;
											prepareStatement.setDate(i, d);
											values[i - 1] = d;
										} else if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											java.sql.Timestamp d = (java.sql.Timestamp) rtn;
											prepareStatement.setTimestamp(i, d);
											values[i - 1] = d;
										} else if (parameterTypeName
												.equals("java.io.File")) {
											// ������Ӧ���ļ���
											// rtn��Ӧ���Ǹ���file����
											ioblob = convertFiletoBlob((File) rtn);
											prepareStatement.setObject(i,
													ioblob, Types.BLOB);
											values[i - 1] = rtn;
										} else if (parameterTypeName
												.equals("java.io.ByteArrayInputStream")) {
											blob = convertStringtoBlob((ByteArrayInputStream) rtn);
											prepareStatement.setObject(i, blob,
													Types.BLOB);
											values[i - 1] = rtn;
										}
									} else {
										prepareStatement.setString(i, String
												.valueOf(rtn == null ? "" : rtn
														.toString()));
										values[i - 1] = String
												.valueOf(rtn == null ? "" : rtn
														.toString());
									}
								} catch (IllegalAccessException e) {
									logger.error(e.getMessage());
									e.printStackTrace();
									throw e;
								} catch (InvocationTargetException e) {
									logger.error(e.getMessage());
									e.printStackTrace();
									throw e;
								}
							}
						}
					} // for
					// showsql(sql,values);
					prepareStatement.executeUpdate();
					if (!getLongtran()) {
						myconn.getConnection().commit();
					}
					return true;
				}
			} finally {
				if (prepareStatement != null) {
					prepareStatement.close();
					prepareStatement = null;
				}
				if (blob != null)
					oracle.sql.BLOB.freeTemporary(blob);
				if (ioblob != null)
					oracle.sql.BLOB.freeTemporary(ioblob);
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return result;
	}

	public boolean runSql(String fullSql) throws Exception, SQLException {
		logger.info("runSql:" + fullSql);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (fullSql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		if (fullSql.trim().equals(""))
			throw new Exception("��ִ��runSqlʱִ����䲻��Ϊ��!");
		PreparedStatement prepareStatement = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				}
			}
			if (open && getLongtran())
			{
				myconn.getConnection().setAutoCommit(false);
			}
			try {
				prepareStatement = myconn.getConnection().prepareStatement(
						fullSql);
				if (prepareStatement == null)
					throw new Exception(
							"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
				prepareStatement.executeUpdate();
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				return true;
			} finally {
				if (prepareStatement != null) {
					prepareStatement.close();
					prepareStatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			logger.error(fullSql);
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
	}

	public boolean runSql(String dmlSql, Object[] parameters) throws Exception,
			SQLException {
		logger.info("runSql:" + dmlSql);

		if (getLongtran() && islongtransuccess == false)
			return false;
		if (dmlSql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		if (dmlSql.trim().equals(""))
			throw new Exception("��ִ��runSqlʱִ����䲻��Ϊ��!");
		PreparedStatement prepareStatement = null;
		oracle.sql.BLOB ioblob = null;
		oracle.sql.BLOB blob = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				}
			}
			if (open && getLongtran())
			{
				myconn.getConnection().setAutoCommit(false);
			}
			try {
				prepareStatement = myconn.getConnection().prepareStatement(
						dmlSql);
				if (prepareStatement == null)
					throw new Exception(
							"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
				if (parameters != null) {
					for (int i = 0; i < parameters.length; i++) {
						if (parameters[i] == null) {
							// logger.info(i+" is null");
							prepareStatement.setString(i + 1, "");
						} else {
							Class parameterTypeClass = parameters[i].getClass();
							String parameterTypeName = parameterTypeClass
									.getName();
							// logger.info(i+"-->"+parameterTypeName+"-->"+parameters[i].toString());
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								prepareStatement.setInt(i + 1,
										((Integer) parameters[i]).intValue());
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")
										|| parameterTypeName
												.equalsIgnoreCase("float")) {
									prepareStatement.setFloat(i + 1,
											((Float) parameters[i])
													.floatValue());
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {

										String tmpvalue = "";
										if (parameters[i] != null) {
											tmpvalue = com.jl.util.StringUtils
													.replaceString(
															parameters[i]
																	.toString(),
															"'", "\"");
										}
										prepareStatement.setString(i + 1,
												tmpvalue);

									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											prepareStatement
													.setTimestamp(
															i + 1,
															parameters[i] == null ? null
																	: (java.sql.Timestamp) parameters[i]);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												prepareStatement
														.setDate(
																i + 1,
																parameters[i] == null ? null
																		: (java.sql.Date) parameters[i]);
											} else {
												if (parameterTypeName
														.equalsIgnoreCase("java.lang.Long")
														|| parameterTypeName
																.equalsIgnoreCase("long")) {
													prepareStatement
															.setLong(
																	i + 1,
																	((Long) parameters[i])
																			.longValue());
												} else {
													if (parameterTypeName
															.equals("java.io.File")) {
														// ������Ӧ���ļ���
														ioblob = convertFiletoBlob((File) parameters[i]);
														prepareStatement
																.setObject(
																		i,
																		ioblob,
																		Types.BLOB);
													} else {
														if (parameterTypeName
																.equals("java.io.ByteArrayInputStream")) {
															blob = convertStringtoBlob((ByteArrayInputStream) parameters[i]);
															prepareStatement
																	.setObject(
																			i,
																			blob,
																			Types.BLOB);
														}
													}

												}
											}
										}
									}
								}
							}
						}
					}
				}
				prepareStatement.executeUpdate();
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				return true;
			} finally {
				if (prepareStatement != null) {
					prepareStatement.close();
					prepareStatement = null;
				}
				if (blob != null)
					oracle.sql.BLOB.freeTemporary(blob);
				if (ioblob != null)
					oracle.sql.BLOB.freeTemporary(ioblob);
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					throw ex;
				} else {
					islongtransuccess = false;

				}
			}
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
	}

	public boolean runSql(String[] sql, String[][] param, Object[] bean,
			Object[][] tialjianvalues) throws Exception, SQLException {
		logger.info("runSql:" + sql);
		int w = 0;
		int x = 0;
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("����һ������ִ�У���ݿ����Ӷ����ڿ�ʼ����֮ǰ�԰Ѵ���!");
		if (sql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		PreparedStatement prepareStatement = null;
		oracle.sql.BLOB ioblob = null;
		oracle.sql.BLOB blob = null;
		boolean result = false;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				}
			}
			if (open && getLongtran())
				myconn.getConnection().setAutoCommit(false);
			for (x = 0; (sql != null || bean != null) && x < sql.length
					&& x < bean.length; x++) {
				if (!sql[x].equalsIgnoreCase("")) {
					prepareStatement = myconn.getConnection().prepareStatement(
							sql[x]);
					// logger.info("��" + (x + 1) + "��SQL�� " + sql[x]);
					if (prepareStatement == null)
						throw new Exception(
								"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
					Class beanClass = bean[x].getClass();
					w = 0;
					for (int i = 1; i <= param[x].length
							&& sql[x].indexOf("delete from") == -1; i++) {
						String fieldName = param[x][i - 1];
						String compareMethodName = "get" + fieldName;
						String beanMethodName = RefUtil.getMethodName(
								beanClass, compareMethodName);
						if (!beanMethodName.equals("")) {
							Method m = null;
							try {
								m = beanClass.getMethod(beanMethodName, null);
							} catch (NoSuchMethodException e) {
								logger.error("DBAccess runSql no found method:"
										+ e.getMessage());
								e.printStackTrace();
								continue;
							}
							if (m != null) {
								// ȡ�ֶ�����
								Object rtn = m.invoke(bean[x], null);
								if (rtn != null) {
									// logger.info("��"+i+"��fieldName��
									// "+fieldName+" ֵ��"+rtn.toString());
									String parameterTypeName = rtn.getClass()
											.getName();
									if (parameterTypeName
											.equals("java.lang.Integer")
											|| parameterTypeName.equals("int")) {
										prepareStatement.setInt(i, new Integer(
												rtn.toString()).intValue());
									} else if (parameterTypeName
											.equals("java.lang.Float")
											|| parameterTypeName
													.equals("float")) {
										prepareStatement.setFloat(i, Float
												.parseFloat(rtn.toString()));
									} else if (parameterTypeName
											.equals("java.lang.Double")
											|| parameterTypeName
													.equals("double")) {
										prepareStatement.setDouble(i, Double
												.parseDouble(rtn.toString()));
									} else if (parameterTypeName.equals("long")) {
										prepareStatement.setLong(i, new Long(
												rtn.toString()).longValue());
									} else if (parameterTypeName.equals("int")) {
										prepareStatement.setInt(i, new Integer(
												rtn.toString()).intValue());
									} else if (parameterTypeName
											.equals("java.lang.String")) {
										String tmpvalue = "";
										if (rtn != null) {
											tmpvalue = com.jl.util.StringUtils
													.replaceString(rtn
															.toString(), "'",
															"\"");
										}
										prepareStatement.setString(i, tmpvalue);
									} else if (parameterTypeName
											.equals("java.util.Date")) {
										java.util.Date utilDate;
										utilDate = (java.util.Date) rtn;
										java.sql.Timestamp sqlDate = new java.sql.Timestamp(
												utilDate.getTime());
										prepareStatement.setTimestamp(i,
												sqlDate);
									} else if (parameterTypeName
											.equals("java.sql.Date")) {
										java.sql.Date d = (java.sql.Date) rtn;
										prepareStatement.setDate(i, d);
									} else if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										java.sql.Timestamp d = (java.sql.Timestamp) rtn;
										prepareStatement.setTimestamp(i, d);
									} else if (parameterTypeName
											.equals("float")) {
										prepareStatement.setFloat(i, Float
												.parseFloat(rtn.toString()));
									} else {
										if (parameterTypeName
												.equals("java.io.File")) {
											// ������Ӧ���ļ���
											ioblob = convertFiletoBlob((File) rtn);
											prepareStatement.setObject(i,
													ioblob, Types.BLOB);
										} else {
											if (parameterTypeName
													.equals("java.io.ByteArrayInputStream")) {
												blob = convertStringtoBlob((ByteArrayInputStream) rtn);
												prepareStatement.setObject(i,
														blob, Types.BLOB);
											}
										}
									}
								} else {
									// date����ûֵ������null
									prepareStatement.setDate(i, null);
								}

							}
						}
						w = i;
					}
					Object tmpparam = null;
					// ��where��ı�����ֵ
					if (tialjianvalues != null) {
						if (tialjianvalues[x] != null) {
							for (int i = 0; i < tialjianvalues[x].length; i++) {
								try {
									tmpparam = tialjianvalues[x][i];
									if (tmpparam == null) {
										continue;
									} else {
										w = w + 1;
										prepareStatement.setString(w, String
												.valueOf(tmpparam));
									}
								} catch (Exception e) {
									logger.error("�˴��쳣��Ӱ��:" + e.getMessage());
								}
							}
						}
					}
					prepareStatement.execute();
				}
			}
			if (!getLongtran()) {
				myconn.getConnection().commit();
			}
			result = true;
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException" + e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException:" + e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					result = false;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (ioblob != null)
				oracle.sql.BLOB.freeTemporary(ioblob);
			if (blob != null)
				oracle.sql.BLOB.freeTemporary(blob);
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					result = false;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (prepareStatement != null) {
				prepareStatement.close();
				prepareStatement = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return result;
	}

	public boolean execProcedure(String procedurename, Object[] parameters)
			throws SQLException, Exception {
		logger.info("execProcedure:" + procedurename);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (procedurename == null)
			throw new Exception("procedurename is null!");
		if (procedurename.trim().equals(""))
			throw new Exception("procedurename is empty String!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("()}");
		} else {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("(");
			for (int i = 0; i < parameters.length; i++) {
				sqlbuffer.append("?,");
			}
			if (parameters.length > 0) {
				sqlbuffer.delete(sqlbuffer.length() - 1, sqlbuffer.length());
			}
			sqlbuffer.append(")}");
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		boolean flag = false;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			}
			try {
				callablestatement = myconn.getConnection().prepareCall(sql);
				if (callablestatement == null)
					throw new Exception("callsblestatement is null!");
				if (parameters != null) {
					for (int i = 0; i < parameters.length; i++) {
						// if(parameters[i]==null){continue;}
						Class parameterTypeClass = parameters[i].getClass();
						String parameterTypeName = parameterTypeClass.getName();
						if (parameterTypeName.equals("java.lang.Integer")
								|| parameterTypeName.equals("int")) {
							callablestatement.setInt(i + 1,
									((Integer) parameters[i]).intValue());
						} else {
							if (parameterTypeName
									.equalsIgnoreCase("java.lang.Float")) {
								callablestatement.setFloat(i + 1,
										((Float) parameters[i]).floatValue());
							} else {
								if (parameterTypeName
										.equals("java.lang.String")) {
									String tmpvalue = "";
									if (parameters[i] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[i]
														.toString(), "'", "\"");
									}
									callablestatement
											.setString(i + 1, tmpvalue);

								} else {
									if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										callablestatement
												.setTimestamp(
														i + 1,
														(java.sql.Timestamp) parameters[i]);
									} else {
										if (parameterTypeName
												.equals("java.sql.Date")) {
											callablestatement
													.setDate(
															i + 1,
															(java.sql.Date) parameters[i]);
										}
									}
								}
							}
						}
					}
				}
				callablestatement.execute();
				if (!getLongtran()) {
					myconn.getConnection().commit();
					flag = true;
				}
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return flag;
	}

	// ---------------------------------------------------

	public boolean execProcedure(String procedurename, Object[][] parameters)
			throws Exception {
		logger.info("execProcedure:" + procedurename);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (procedurename == null)
			throw new Exception("procedurename is null!");
		if (procedurename.trim().equals(""))
			throw new Exception("procedurename is empty String!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("()}");
		} else {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("(");
			for (int i = 0; i < parameters.length; i++) {
				sqlbuffer.append("?,");
			}
			if (parameters.length > 0) {
				sqlbuffer.delete(sqlbuffer.length() - 1, sqlbuffer.length());
			}
			sqlbuffer.append(")}");
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		boolean flag = false;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			}
			try {
				callablestatement = myconn.getConnection().prepareCall(sql);
				if (callablestatement == null)
					throw new Exception("callsblestatement is null!");
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								callablestatement.registerOutParameter(i,
										Types.INTEGER);
								// callablestatement.getInt(i);
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									callablestatement.registerOutParameter(i,
											Types.FLOAT);
									// callablestatement.getFloat(i);
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										callablestatement.registerOutParameter(
												i, Types.VARCHAR);
										// callablestatement.getString(i);
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											callablestatement
													.registerOutParameter(i,
															Types.TIMESTAMP);
											// callablestatement.getTimestamp(i);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												callablestatement
														.registerOutParameter(
																i, Types.DATE);
												// callablestatement.getDate(i);
											}
										}
									}
								}
							}
						} else {
							if (parameterdirection.equalsIgnoreCase("in")) // �������
							{
								Class parameterTypeClass = parameters[j][1]
										.getClass();
								String parameterTypeName = parameterTypeClass
										.getName(); // ����ֵ������Class
								if (parameterTypeName
										.equals("java.lang.Integer")
										|| parameterTypeName.equals("int")) {
									callablestatement.setInt(i,
											((Integer) parameters[j][1])
													.intValue());

								} else if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									callablestatement.setFloat(i,
											((Float) parameters[j][1])
													.floatValue());

								} else if (parameterTypeName
										.equals("java.lang.String")) {
									String tmpvalue = "";
									if (parameters[j][1] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[j][1]
														.toString(), "'", "\"");
										callablestatement
												.setString(i, tmpvalue);
									} else {
										callablestatement.setString(i + 1,
												tmpvalue);
									}
								} else if (parameterTypeName
										.equals("java.sql.Timestamp")) {
									callablestatement
											.setTimestamp(
													i,
													(java.sql.Timestamp) parameters[j][1]);
								} else if (parameterTypeName
										.equals("java.sql.Date")) {

									callablestatement.setDate(i,
											(java.sql.Date) parameters[j][1]);
								}
							}
						}
					}
				}
				callablestatement.execute();
				// ----------------------
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								parameters[j][1] = new Integer(
										callablestatement.getInt(i));
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									parameters[j][1] = new Float(
											callablestatement.getFloat(i));
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										if (callablestatement.getString(i) == null) {
											parameters[j][1] = "";
										} else {
											parameters[j][1] = callablestatement
													.getString(i);
										}
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											parameters[j][1] = callablestatement
													.getTimestamp(i);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												parameters[j][1] = callablestatement
														.getDate(i);
											}
										}
									}
								}
							}
						}
					}
				}

				// ----------------------
				if (!getLongtran()) {
					myconn.getConnection().commit();
					flag = true;
				}
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return flag;
	}

	public boolean execProcedureWithInOut(String procedurename,
			Object[][] parameters) throws Exception {
		logger.info("execProcedureWithInOut:" + procedurename);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (procedurename == null)
			throw new Exception("procedurename is null!");
		if (procedurename.trim().equals(""))
			throw new Exception("procedurename is empty String!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("()}");
		} else {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("(");
			for (int i = 0; i < parameters.length; i++) {
				sqlbuffer.append("?,");
			}
			if (parameters.length > 0) {
				sqlbuffer.delete(sqlbuffer.length() - 1, sqlbuffer.length());
			}
			sqlbuffer.append(")}");
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		boolean flag = false;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			}
			try {
				callablestatement = myconn.getConnection().prepareCall(sql);
				if (callablestatement == null)
					throw new Exception("callsblestatement is null!");
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")
								|| parameterdirection.equalsIgnoreCase("all")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Long")
									|| parameterTypeName.equals("long")) {
								callablestatement.registerOutParameter(i,
										Types.NUMERIC);
							} else {
								if (parameterTypeName
										.equals("java.lang.Integer")
										|| parameterTypeName.equals("int")) {
									callablestatement.registerOutParameter(i,
											Types.INTEGER);
									// callablestatement.getInt(i);
								} else {
									if (parameterTypeName
											.equalsIgnoreCase("java.lang.Float")) {
										callablestatement.registerOutParameter(
												i, Types.FLOAT);
										// callablestatement.getFloat(i);
									} else {
										if (parameterTypeName
												.equals("java.lang.String")) {
											callablestatement
													.registerOutParameter(i,
															Types.VARCHAR);
											// callablestatement.getString(i);
										} else {
											if (parameterTypeName
													.equals("java.sql.Timestamp")) {
												callablestatement
														.registerOutParameter(
																i,
																Types.TIMESTAMP);
												// callablestatement.getTimestamp(i);
											} else {
												if (parameterTypeName
														.equals("java.sql.Date")) {
													callablestatement
															.registerOutParameter(
																	i,
																	Types.DATE);
													// callablestatement.getDate(i);
												}
											}
										}
									}
								}
							}
						}

						if (parameterdirection.equalsIgnoreCase("in")
								|| parameterdirection.equalsIgnoreCase("all")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Long")
									|| parameterTypeName.equals("long")) {
								callablestatement.setLong(i,
										((Long) parameters[j][1]).longValue());

							} else if (parameterTypeName
									.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								callablestatement
										.setInt(i, ((Integer) parameters[j][1])
												.intValue());

							} else if (parameterTypeName
									.equalsIgnoreCase("java.lang.Float")) {
								callablestatement
										.setFloat(i, ((Float) parameters[j][1])
												.floatValue());

							} else if (parameterTypeName
									.equals("java.lang.String")) {
								String tmpvalue = "";
								if (parameters[j][1] != null) {
									tmpvalue = com.jl.util.StringUtils
											.replaceString(parameters[j][1]
													.toString(), "'", "\"");
									callablestatement.setString(i, tmpvalue);
								} else {
									callablestatement
											.setString(i + 1, tmpvalue);
								}
							} else if (parameterTypeName
									.equals("java.sql.Timestamp")) {
								callablestatement.setTimestamp(i,
										(java.sql.Timestamp) parameters[j][1]);
							} else if (parameterTypeName
									.equals("java.sql.Date")) {

								callablestatement.setDate(i,
										(java.sql.Date) parameters[j][1]);
							}
						}
					}
				}
				callablestatement.execute();
				// ----------------------
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")
								|| parameterdirection.equalsIgnoreCase("all")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								parameters[j][1] = new Integer(
										callablestatement.getInt(i));
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									parameters[j][1] = new Float(
											callablestatement.getFloat(i));
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										if (callablestatement.getString(i) == null) {
											parameters[j][1] = "";
										} else {
											parameters[j][1] = callablestatement
													.getString(i);
										}
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											parameters[j][1] = callablestatement
													.getTimestamp(i);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												parameters[j][1] = callablestatement
														.getDate(i);
											}
										}
									}
								}
							}
						}
					}
				}

				// ----------------------
				if (!getLongtran()) {
					myconn.getConnection().commit();
					flag = true;
				}
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return flag;
	}

	public boolean execProcedure(String procedurename, Object[][] parameters,
			String objecttypename, String listtypename, Class oprclass)
			throws Exception {
		logger.info("execProcedure:" + procedurename);
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (procedurename == null)
			throw new Exception("procedurename is null!");
		if (procedurename.trim().equals(""))
			throw new Exception("procedurename is empty String!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("()}");
		} else {
			sqlbuffer.append("{call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("(");
			for (int i = 0; i < parameters.length; i++) {
				sqlbuffer.append("?,");
			}
			if (parameters.length > 0) {
				sqlbuffer.delete(sqlbuffer.length() - 1, sqlbuffer.length());
			}
			sqlbuffer.append(")}");
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		boolean flag = false;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			}
			try {
				callablestatement = myconn.getConnection().prepareCall(sql);
				if (callablestatement == null)
					throw new Exception("callsblestatement is null!");
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								callablestatement.registerOutParameter(i,
										Types.INTEGER);
								// callablestatement.getInt(i);
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									callablestatement.registerOutParameter(i,
											Types.FLOAT);
									// callablestatement.getFloat(i);
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										callablestatement.registerOutParameter(
												i, Types.VARCHAR);
										// callablestatement.getString(i);
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											callablestatement
													.registerOutParameter(i,
															Types.TIMESTAMP);
											// callablestatement.getTimestamp(i);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												callablestatement
														.registerOutParameter(
																i, Types.DATE);
												// callablestatement.getDate(i);
											}
										}
									}
								}
							}
						} else {
							if (parameterdirection.equalsIgnoreCase("in")) // �������
							{
								Class parameterTypeClass = parameters[j][1]
										.getClass();
								String parameterTypeName = parameterTypeClass
										.getName(); // ����ֵ������Class
								if (parameterTypeName
										.equals("java.lang.Integer")
										|| parameterTypeName.equals("int")) {
									callablestatement.setInt(i,
											((Integer) parameters[j][1])
													.intValue());

								} else if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									callablestatement.setFloat(i,
											((Float) parameters[j][1])
													.floatValue());

								} else if (parameterTypeName
										.equals("java.lang.String")) {
									String tmpvalue = "";
									if (parameters[j][1] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[j][1]
														.toString(), "'", "\"");
										callablestatement
												.setString(i, tmpvalue);
									} else {
										callablestatement.setString(i + 1,
												tmpvalue);
									}
								} else if (parameterTypeName
										.equals("java.sql.Timestamp")) {
									callablestatement
											.setTimestamp(
													i,
													(java.sql.Timestamp) parameters[j][1]);
								} else if (parameterTypeName
										.equals("java.sql.Date")) {

									callablestatement.setDate(i,
											(java.sql.Date) parameters[j][1]);
								} else if (parameterTypeName
										.equals("java.util.ArrayList")) {
									ARRAY aArray = getArray(myconn
											.getConnection(), objecttypename,
											listtypename,
											(ArrayList) parameters[j][1],
											oprclass);//    
									callablestatement.setArray(i, aArray);
								}
							}
						}
					}
				}
				callablestatement.execute();
				// ----------------------
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								parameters[j][1] = new Integer(
										callablestatement.getInt(i));
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									parameters[j][1] = new Float(
											callablestatement.getFloat(i));
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										if (callablestatement.getString(i) == null) {
											parameters[j][1] = "";
										} else {
											parameters[j][1] = callablestatement
													.getString(i);
										}
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											parameters[j][1] = callablestatement
													.getTimestamp(i);
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												parameters[j][1] = callablestatement
														.getDate(i);
											}
										}
									}
								}
							}
						}
					}
				}

				// ----------------------
				if (!getLongtran()) {
					myconn.getConnection().commit();
					flag = true;
				}
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					flag = false;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return flag;
	}

	private ARRAY getArray(Connection con, String OracleObj, String Oraclelist,
			ArrayList objlist, Class oprclass) throws Exception {
		ARRAY list = null;
		if (objlist != null && objlist.size() > 0) {
			StructDescriptor structdesc = new StructDescriptor(OracleObj, con);
			STRUCT[] structs = new STRUCT[objlist.size()];
			Object[] result = null;
			Object object = null;
			Method invokeMethod = null;
			Class beantype[] = null;
			for (int i = 0; i < objlist.size(); i++) {
				result = new Object[structdesc.getLocalAttributeCount()];// �����СӦ���㶨�����ݿ����(AOBJECT)�����Եĸ���
				object = oprclass.newInstance();
				beantype = new Class[2];
				beantype[0] = objlist.get(i).getClass();
				beantype[1] = result.getClass();
				invokeMethod = oprclass.getMethod("setArrayFromList", beantype);
				Object[] ParameterValue = { objlist.get(i), result };
				Object retobj = invokeMethod.invoke(object, ParameterValue);
				result = (Object[]) retobj;
				structs[i] = new STRUCT(structdesc, con, result);
			}
			ArrayDescriptor desc = ArrayDescriptor.createDescriptor(Oraclelist,
					con);
			list = new ARRAY(desc, con, structs);
		} // if
		return list;
	} // function

	public boolean isOpen() {
		return open;
	}

	public void close() throws Exception, SQLException {
		if (!getLongtran()) {
			try {
				if (open) {
					try {
						if (rset != null)
							rset.close();
					} catch (Exception exception) {
						logger.error(exception.getMessage());
						exception.printStackTrace();
						throw new IllegalStateException("Exception: "
								+ exception);
					} finally {
						try {
							if (stmt != null)
								stmt.close();
						} catch (Exception exception) {
							logger.error(exception.getMessage());
							exception.printStackTrace();
							throw new IllegalStateException("Exception: "
									+ exception);
						} finally {
							try {
								if (open && myconn != null) {
									if (myconn.getConnection().isClosed() == false) {
										myconn.getConnection().setAutoCommit(true);
										myconn.getConnection().close();
									}
									ConnectionMap.remove(myconn.getId());
									//Utils.releaseConnection();
									// Utils.releaseConnection(Utils.getAppsContext());
									// Utils.getAppsContext().freeWebAppsContext();
									// Utils.releaseAppsContext();
								}
							} catch (Exception exception) {
								logger.error(exception.getMessage());
								exception.printStackTrace();
								throw new IllegalStateException("Exception: "
										+ exception);
							} finally {
								open = false;
							}
						}
					}
				} else {
					try {
						if (myconn != null) {
							if (myconn.getConnection().isClosed() == false) {
								myconn.getConnection().setAutoCommit(true);
								myconn.getConnection().close();
							}
							ConnectionMap.remove(myconn.getId());
							//Utils.releaseConnection();
							// Utils.releaseConnection(Utils.getAppsContext());
							// Utils.getAppsContext().freeWebAppsContext();
						}
					} catch (Exception exception) {
						logger.error(exception.getMessage());
						exception.printStackTrace();
						throw new IllegalStateException("Exception: "
								+ exception);
					} finally {
						open = false;
					}
				}
			} finally {
				myconn = null;
			}
		}
	}

	protected void finalize() throws Exception {
		IllegalStateException illegalstateexception = new IllegalStateException(
				"DBAccess finalize �����쳣!");
		try {
			close();
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw exception;
		} finally {
		}
		throw illegalstateexception;
	}

	/*
	 * ���ܣ�ִ��һ���洢����,�������Ҫ����ִ��function��return value�Ͷ��outֵ ����:functionname ������
	 * parameters ������ ����parameters�������������[in,out]��ֵ,�� ����:
	 * parameters[0][0]="in";//���� ���� parameters[0][1]=new Integer("2");ֵ
	 * parameters�������±�0��ʾ�ǵ��ú���ĵ�һ���β� ���: parameters[0][0]="out";//���� ���
	 * parameters[0][1]="";//��ֵ return type ����ֵ���� ���� OracleTypes.VARCHAR
	 * OracleTypes.NUMBER .... ��returntype=OracleTypes.NULL��ʾ�˺����޷���ֵ
	 * ִ�к����,return value��out��������;���Object
	 */

	public Object execFunction(String functionname, Object[][] parameters,
			int returntype) throws Exception {
		logger.info("execFunction:" + functionname);
		if (getLongtran() && islongtransuccess == false)
			return null;
		Object returnvalue = null;
		ResultSet cursor = null;
		// if (getLongtran())
		// throw new Exception("execFunction��������һ����������ִ��.");
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (functionname == null)
			throw new Exception("��ִ��execFunctionʱ,functionname����ΪNull!");
		if (functionname.trim().equals(""))
			throw new Exception("��ִ��execFunctionʱ,functionname����Ϊ��!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			if (returntype == OracleTypes.NULL) {
				sqlbuffer.append("{call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("()}");
			} else {
				sqlbuffer.append("{?=call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("()}");
			}
		} else {
			if (returntype == OracleTypes.NULL) {
				sqlbuffer.append("{call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("(");
				for (int i = 0; i < parameters.length; i++) {
					sqlbuffer.append("?,");
				}
				if (parameters.length > 0) {
					sqlbuffer
							.delete(sqlbuffer.length() - 1, sqlbuffer.length());
				}
				sqlbuffer.append(")}");
			} else {
				sqlbuffer.append("{?=call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("(");
				for (int i = 0; i < parameters.length; i++) {
					sqlbuffer.append("?,");
				}
				if (parameters.length > 0) {
					sqlbuffer
							.delete(sqlbuffer.length() - 1, sqlbuffer.length());
				}
				sqlbuffer.append(")}");
			}
		}
		String sql = sqlbuffer.toString();
		// logger.info("sql-->"+sql);
		java.sql.CallableStatement callablestatement = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception(
							"��ִ��execFunctionʱUtils.getConnection����NULL");
			}
			try {
				callablestatement = myconn.getConnection().prepareCall(sql,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				if (returntype != OracleTypes.NULL) {
					callablestatement.registerOutParameter(1, returntype);
				}
				if (callablestatement == null)
					throw new Exception("��ִ��execFunctionʱprepareCall()����NULL!");
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								if (returntype == OracleTypes.NULL) { // �޷���ֵ
									callablestatement.registerOutParameter(i,
											Types.INTEGER);
								} else {
									callablestatement.registerOutParameter(
											i + 1, Types.INTEGER);
								}
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									if (returntype == OracleTypes.NULL) { // �޷���ֵ
										callablestatement.registerOutParameter(
												i, Types.FLOAT);
									} else {
										callablestatement.registerOutParameter(
												i + 1, Types.FLOAT);
									}
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										if (returntype == OracleTypes.NULL) { // �޷���ֵ
											callablestatement
													.registerOutParameter(i,
															Types.VARCHAR);
										} else {
											callablestatement
													.registerOutParameter(
															i + 1,
															Types.VARCHAR);
										}
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											if (returntype == OracleTypes.NULL) { // �޷���ֵ
												callablestatement
														.registerOutParameter(
																i,
																Types.TIMESTAMP);
											} else {
												callablestatement
														.registerOutParameter(
																i + 1,
																Types.TIMESTAMP);
											}
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												if (returntype == OracleTypes.NULL) { // �޷���ֵ
													callablestatement
															.registerOutParameter(
																	i,
																	Types.DATE);
												} else {
													callablestatement
															.registerOutParameter(
																	i + 1,
																	Types.DATE);
												}
											}
										}
									}
								}
							}
						} else {
							if (parameterdirection.equalsIgnoreCase("in")) // �������
							{
								Class parameterTypeClass = parameters[j][1]
										.getClass();
								String parameterTypeName = parameterTypeClass
										.getName(); // ����ֵ������Class
								if (parameterTypeName
										.equals("java.lang.Integer")
										|| parameterTypeName.equals("int")) {
									if (returntype == OracleTypes.NULL) { // �޷���ֵ
										callablestatement.setInt(i,
												((Integer) parameters[j][1])
														.intValue());
									} else {
										callablestatement.setInt(i + 1,
												((Integer) parameters[j][1])
														.intValue());
									}
								} else {
									if (parameterTypeName
											.equalsIgnoreCase("java.lang.Float")) {
										if (returntype == OracleTypes.NULL) { // �޷���ֵ
											callablestatement.setFloat(i,
													((Float) parameters[j][1])
															.floatValue());
										} else {
											callablestatement.setFloat(i + 1,
													((Float) parameters[j][1])
															.floatValue());
										}
									} else {
										if (parameterTypeName
												.equals("java.lang.String")) {
											if (returntype == OracleTypes.NULL) { // �޷���ֵ
												String tmpvalue = "";
												if (parameters[j][1] != null) {
													tmpvalue = com.jl.util.StringUtils
															.replaceString(
																	parameters[j][1]
																			.toString(),
																	"'", "\"");
												}
												callablestatement.setString(i,
														tmpvalue);
												/*
												 * callablestatement.setString(i,
												 * String
												 * .valueOf(parameters[j][1] ==
												 * null ? "" :
												 * parameters[j][1].toString
												 * ()));
												 */
											} else {
												String tmpvalue = "";
												if (parameters[j][1] != null) {
													tmpvalue = com.jl.util.StringUtils
															.replaceString(
																	parameters[j][1]
																			.toString(),
																	"'", "\"");
												}
												callablestatement.setString(
														i + 1, tmpvalue);
												/*
												 * callablestatement.setString(i +
												 * 1, String.valueOf(parameters
												 * [j][1] == null ? "" :
												 * parameters
												 * [j][1].toString()));
												 */
											}
										} else {
											if (parameterTypeName
													.equals("java.sql.Timestamp")) {
												if (returntype == OracleTypes.NULL) { // �޷���ֵ
													callablestatement
															.setTimestamp(
																	i,
																	(java.sql.Timestamp) parameters[j][1]);
												} else {
													callablestatement
															.setTimestamp(
																	i + 1,
																	(java.sql.Timestamp) parameters[j][1]);
												}
											} else {
												if (parameterTypeName
														.equals("java.sql.Date")) {
													if (returntype == OracleTypes.NULL) { // �޷���ֵ
														callablestatement
																.setDate(
																		i,
																		(java.sql.Date) parameters[j][1]);
													} else {
														callablestatement
																.setDate(
																		i + 1,
																		(java.sql.Date) parameters[j][1]);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				callablestatement.execute();
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				if (returntype != OracleTypes.NULL) {
					if (returntype != OracleTypes.CURSOR) {
						returnvalue = callablestatement.getObject(1); // �õ�����ֵ
					} else {
						cursor = ((OracleCallableStatement) callablestatement)
								.getCursor(1);
						List ls = ConvertResultSetToList(cursor);
						returnvalue = ls;
					}
				}
				// ����out��ֵ
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							if (returntype != OracleTypes.NULL)
								parameters[j][1] = callablestatement
										.getObject(i + 1);
							else
								parameters[j][1] = callablestatement
										.getObject(i);
						}
					}
				}
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					returnvalue = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					returnvalue = null;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return returnvalue;
	}

	public List execProcedureByList(String procedurename, Object[] parameters)
			throws Exception {
		logger.info("execProcedureByList:" + procedurename);
		if (getLongtran() && islongtransuccess == false)
			return null;
		List ls = null;
		ResultSet cursor = null;
		// if (getLongtran())
		// throw new Exception("execProcedureByList��������һ����������ִ��.");
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (procedurename == null)
			throw new Exception("��ִ��execProcedureByListʱ,procedurename����ΪNull!");
		if (procedurename.trim().equals(""))
			throw new Exception("��ִ��execProcedureByListʱ,procedurename����Ϊ��!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			sqlbuffer.append("{?=call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("()}");
		} else {
			sqlbuffer.append("{?=call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("(");
			for (int i = 0; i < parameters.length; i++) {
				sqlbuffer.append("?,");
			}
			if (parameters.length > 0) {
				sqlbuffer.delete(sqlbuffer.length() - 1, sqlbuffer.length());
			}
			sqlbuffer.append(")}");
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("connection is null");
			}
			try {
				callablestatement = myconn.getConnection().prepareCall(sql,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				callablestatement.registerOutParameter(1, OracleTypes.CURSOR);
				if (callablestatement == null)
					throw new Exception("callsblestatement is null!");
				if (parameters != null) {
					for (int i = 1, j = 0; j < parameters.length
							&& i <= parameters.length; i++, j++) {
						if (parameters[j] == null) {
							callablestatement.setObject(i + 1, null);
							continue;
						}
						Class parameterTypeClass = parameters[j].getClass();
						String parameterTypeName = parameterTypeClass.getName();
						if (parameterTypeName.equals("java.lang.Integer")
								|| parameterTypeName.equals("int")) {
							callablestatement.setInt(i + 1,
									((Integer) parameters[j]).intValue());
						} else {
							if (parameterTypeName
									.equalsIgnoreCase("java.lang.Float")) {
								callablestatement.setFloat(i + 1,
										((Float) parameters[j]).floatValue());
							} else {
								if (parameterTypeName
										.equals("java.lang.String")) {
									String tmpvalue = "";
									if (parameters[j] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[j]
														.toString(), "'", "\"");
									}
									callablestatement
											.setString(i + 1, tmpvalue);
								} else {
									if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										callablestatement
												.setTimestamp(
														i + 1,
														(java.sql.Timestamp) parameters[j]);
									} else {
										if (parameterTypeName
												.equals("java.sql.Date")) {
											callablestatement
													.setDate(
															i + 1,
															(java.sql.Date) parameters[j]);
										}
									}
								}
							}
						}
					}
				}
				callablestatement.execute();
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				cursor = ((OracleCallableStatement) callablestatement)
						.getCursor(1);
				ls = ConvertResultSetToList(cursor);

			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					ls = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					ls = null;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return ls;
	}

	public boolean runSql(String[] sql, String[][] param, Object[] bean)
			throws Exception, SQLException {
		/*
		for (int i = 0; i < sql.length; i++) {
			logger.info("runSql:" + sql[i]);
		}
		 */
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (!getLongtran() && open)
			throw new Exception("����һ������ִ�У���ݿ����Ӷ����ڿ�ʼ����֮ǰ�԰Ѵ���!");
		if (sql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		PreparedStatement prepareStatement = null;
		oracle.sql.BLOB ioblob = null;
		oracle.sql.BLOB blob = null;
		boolean result = false;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				}
			}
			if (open && getLongtran())
				myconn.getConnection().setAutoCommit(false);
			for (int x = 0; (sql != null || bean != null) && x < sql.length
					&& x < bean.length; x++) {
				if (!sql[x].equalsIgnoreCase("")) {
					prepareStatement = myconn.getConnection().prepareStatement(
							sql[x]);
					// logger.info("��"+(x+1)+"��SQL�� "+sql[x]);
					if (prepareStatement == null)
						throw new Exception(
								"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
					Class beanClass = bean[x].getClass();
					for (int i = 1; i <= param[x].length
							&& sql[x].indexOf("delete from") == -1; i++) {
						String fieldName = param[x][i - 1];
						String compareMethodName = "get" + fieldName;
						String beanMethodName = RefUtil.getMethodName(
								beanClass, compareMethodName);
						if (!beanMethodName.equals("")) {
							Method m = null;
							try {
								m = beanClass.getMethod(beanMethodName, null);
							} catch (NoSuchMethodException e) {
								logger.error("DBAccess runSql no found method:"
										+ e.getMessage());
								e.printStackTrace();
								continue;
							}
							if (m != null) {
								// ȡ�ֶ�����
								Object rtn = m.invoke(bean[x], null);
								if (rtn != null) {
									// logger.info("��"+i+"��fieldName��"+fieldName+"
									// ֵ��"+rtn.toString());
									String parameterTypeName = rtn.getClass()
											.getName();
									if (parameterTypeName
											.equals("java.lang.Integer")
											|| parameterTypeName.equals("int")) {
										prepareStatement.setInt(i, new Integer(
												rtn.toString()).intValue());
									} else if (parameterTypeName
											.equals("java.lang.Float")
											|| parameterTypeName
													.equals("float")) {
										prepareStatement.setFloat(i, Float
												.parseFloat(rtn.toString()));
									} else if (parameterTypeName
											.equals("java.lang.Double")
											|| parameterTypeName
													.equals("double")) {
										prepareStatement.setDouble(i, Double
												.parseDouble(rtn.toString()));
									} else if (parameterTypeName.equals("long")) {
										prepareStatement.setLong(i, new Long(
												rtn.toString()).longValue());
									} else if (parameterTypeName.equals("int")) {
										prepareStatement.setInt(i, new Integer(
												rtn.toString()).intValue());
									} else if (parameterTypeName
											.equals("java.lang.String")) {
										String tmpvalue = "";
										if (rtn != null) {
											tmpvalue = com.jl.util.StringUtils
													.replaceString(rtn
															.toString(), "'",
															"\"");
										}
										prepareStatement.setString(i, tmpvalue);
									} else if (parameterTypeName
											.equals("java.util.Date")) {
										java.util.Date utilDate;
										utilDate = (java.util.Date) rtn;
										java.sql.Timestamp sqlDate = new java.sql.Timestamp(
												utilDate.getTime());
										prepareStatement.setTimestamp(i,
												sqlDate);
									} else if (parameterTypeName
											.equals("java.sql.Date")) {
										java.sql.Date d = (java.sql.Date) rtn;
										prepareStatement.setDate(i, d);
									} else if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										java.sql.Timestamp d = (java.sql.Timestamp) rtn;
										prepareStatement.setTimestamp(i, d);
									} else if (parameterTypeName
											.equals("float")) {
										prepareStatement.setFloat(i, Float
												.parseFloat(rtn.toString()));
									} else {
										if (parameterTypeName
												.equals("java.io.File")) {
											// ������Ӧ���ļ���
											ioblob = convertFiletoBlob((File) rtn);
											prepareStatement.setObject(i,
													ioblob, Types.BLOB);
										} else {
											if (parameterTypeName
													.equals("java.io.ByteArrayInputStream")) {
												blob = convertStringtoBlob((ByteArrayInputStream) rtn);
												prepareStatement.setObject(i,
														blob, Types.BLOB);

											}
										}
									}
								} else {
									// date����ûֵ������null
									prepareStatement.setDate(i, null);
								}

							}
						}
					}
					prepareStatement.execute();
				}
			}
			if (!getLongtran()) {
				myconn.getConnection().commit();
			}
			result = true;
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException" + e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException:" + e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					result = false;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					result = false;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (blob != null)
				oracle.sql.BLOB.freeTemporary(blob);
			if (ioblob != null)
				oracle.sql.BLOB.freeTemporary(ioblob);
			if (prepareStatement != null) {
				prepareStatement.close();
				prepareStatement = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return result;
	}

	public int execBatchSql(List batchSql) throws SQLException, Exception {
		logger.info("execBatchSql:" + batchSql);
		if (getLongtran() && islongtransuccess == false)
			return -1;
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		int result = 0;
		Statement stmt = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception("��ִ��runSqlʱUtils.getConnection����NULL");
			} else {
				if (getLongtran() && !open) {
					if (myconn == null) {
						myconn = getMYConnection();
					}
					myconn.getConnection().setAutoCommit(false);
					open = true;
					if (myconn == null)
						throw new Exception(
								"��ִ��runSqlʱUtils.getConnection����NULL");
				}
			}
			if (open && getLongtran())
				myconn.getConnection().setAutoCommit(false);
			try {
				stmt = myconn.getConnection().createStatement();
				if (stmt == null)
					throw new Exception("��execBatchSqlʱcreateStatement()����NULL");
				java.util.Iterator it = batchSql.iterator();
				while (it.hasNext()) {
					stmt.addBatch(it.next().toString());
				}
				stmt.executeBatch();
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				result = 1;
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					result = -1;
				} else {
					islongtransuccess = false;
				}
			}

			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					result = -1;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return result;
	}

	public List execProcedureByList_readOnly(String procedurename,
			Object[] parameters) throws Exception {
		logger.info("execProcedureByList_readOnly:" + procedurename);
		if (getLongtran() && islongtransuccess == false)
			return null;
		List ls = null;
		ResultSet cursor = null;
		// if (getLongtran())
		// throw new Exception("execProcedureByList��������һ����������ִ��.");
		if (!getLongtran() && open)
			throw new Exception("Doing is not Tran,But Conneciton is Open");
		if (procedurename == null)
			throw new Exception("��ִ��execProcedureByListʱ,procedurename����ΪNull!");
		if (procedurename.trim().equals(""))
			throw new Exception("��ִ��execProcedureByListʱ,procedurename����Ϊ��!");
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			sqlbuffer.append("{?=call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("()}");
		} else {
			sqlbuffer.append("{?=call ");
			sqlbuffer.append(procedurename);
			sqlbuffer.append("(");
			for (int i = 0; i < parameters.length; i++) {
				sqlbuffer.append("?,");
			}
			if (parameters.length > 0) {
				sqlbuffer.delete(sqlbuffer.length() - 1, sqlbuffer.length());
			}
			sqlbuffer.append(")}");
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception(
							"connection is null");
			}
			try {
				callablestatement = myconn.getConnection()
						.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_READ_ONLY);
				callablestatement.registerOutParameter(1, OracleTypes.CURSOR);
				if (callablestatement == null)
					throw new Exception("callsblestatement is null!");
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						Class parameterTypeClass = parameters[j].getClass();
						String parameterTypeName = parameterTypeClass.getName();
						if (parameterTypeName.equals("java.lang.Integer")
								|| parameterTypeName.equals("int")) {
							callablestatement.setInt(i + 1,
									((Integer) parameters[j]).intValue());
						} else {
							if (parameterTypeName
									.equalsIgnoreCase("java.lang.Float")) {
								callablestatement.setFloat(i + 1,
										((Float) parameters[j]).floatValue());
							} else {
								if (parameterTypeName
										.equals("java.lang.String")) {
									String tmpvalue = "";
									if (parameters[j] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[j]
														.toString(), "'", "\"");
									}
									callablestatement
											.setString(i + 1, tmpvalue);
								} else {
									if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										callablestatement
												.setTimestamp(
														i + 1,
														(java.sql.Timestamp) parameters[j]);
									} else {
										if (parameterTypeName
												.equals("java.sql.Date")) {
											callablestatement
													.setDate(
															i + 1,
															(java.sql.Date) parameters[j]);
										}
									}
								}
							}
						}
					}
				}
				callablestatement.execute();
				if (!getLongtran()) {
					myconn.getConnection().commit();
				}
				cursor = ((OracleCallableStatement) callablestatement)
						.getCursor(1);
				ls = ConvertResultSetToList(cursor);
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					ls = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					ls = null;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
		return ls;
	}

	public boolean rollbacklongTran(Object tranObject) throws Exception {
		if (getLongtran()) {
			if (tranObject != null) {
				if (tranObject.toString().equalsIgnoreCase(myconn.getId())) {
					myconn.getConnection().rollback();
					
					Context.getInstance().put("tranconnectionid", "");
					ConnectionMap.remove(tranObject.toString());
					tranObject = null;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public List executeQueryProc(String procname, Object[] param)
			throws SQLException, Exception {
		logger.info("executeQueryProc:" + procname);
		ResultSet rs = null;
		int cnt = 0;
		if (param == null) {
			return null;
		}
		String sql = "{?=call " + procname + "(";
		for (; cnt < param.length - 1; cnt++) {
			sql += "?,";
		}
		sql += "?)}";
		cnt++;
		java.sql.CallableStatement cstmt = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception(
							"connection is null");
			}
			cstmt = myconn.getConnection().prepareCall(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (param != null) {
				for (int i = 1, j = 0; i <= cnt; i++, j++) {
					Class parameterTypeClass = param[j].getClass();
					String parameterTypeName = parameterTypeClass.getName();
					if (parameterTypeName.equals("java.lang.Integer")
							|| parameterTypeName.equals("int")) {
						cstmt.setInt(i + 1, (new Integer(param[j].toString())
								.intValue()));
					} else {
						if (parameterTypeName
								.equalsIgnoreCase("java.lang.Float")) {
							cstmt.setFloat(i + 1, (new Float(param[j]
									.toString()).floatValue()));
						} else {
							if (parameterTypeName.equals("java.lang.String")) {
								String tmpvalue = "";
								if (param[j] != null) {
									tmpvalue = com.jl.util.StringUtils
											.replaceString(param[j].toString(),
													"'", "\"");
								}
								cstmt.setString(i + 1, tmpvalue);
							} else {
								if (parameterTypeName
										.equals("java.sql.Timestamp")) {
									cstmt
											.setTimestamp(
													i + 1,
													DateUtil
															.getTimestampAndTimeFromString(param[j]
																	.toString()));
								} else {
									if (parameterTypeName
											.equals("java.sql.Date")) {
										cstmt.setDate(i + 1, DateUtil
												.getDateFromString(param[j]
														.toString()));
									}
								}
							}
						}
					}
				}
			}
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.execute();
			rs = (ResultSet) cstmt.getObject(1);
			List ls = ConvertResultSetToList(rs);
			return ls;
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					cstmt = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					cstmt = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
	}

	/**
	 * �洢��̲�ѯ ?=call {aaa(?,?)}
	 * 
	 * @return ��ѯ����α�
	 * @throws SQLException
	 */
	public List executeQueryProc(String procname, String[] param)
			throws SQLException, Exception {
		logger.info("executeQueryProc:" + procname);
		ResultSet rs = null;
		int cnt = 0;
		if (param == null) {
			return null;
		}
		String sql = "{?=call " + procname + "(";
		for (; cnt < param.length; cnt++) {
			sql += "?,";
		}
		sql += "?)}";
		java.sql.CallableStatement cstmt = null;
		try {
			if (!open && !getLongtran()) {
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
				if (myconn == null)
					throw new Exception(
							"connection is null");
			}
			cstmt = myconn.getConnection().prepareCall(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (param != null) {
				for (int i = 1, j = 0; i <= cnt; i++, j++) {
					Class parameterTypeClass = param[j].getClass();
					String parameterTypeName = parameterTypeClass.getName();
					if (parameterTypeName.equals("java.lang.Integer")
							|| parameterTypeName.equals("int")) {
						cstmt.setInt(i + 1, (new Integer(param[j]).intValue()));
					} else {
						if (parameterTypeName
								.equalsIgnoreCase("java.lang.Float")) {
							cstmt.setFloat(i + 1, (new Float(param[j])
									.floatValue()));
						} else {
							if (parameterTypeName.equals("java.lang.String")) {
								String tmpvalue = "";
								if (param[j] != null) {
									tmpvalue = com.jl.util.StringUtils
											.replaceString(param[j].toString(),
													"'", "\"");
								}
								cstmt.setString(i + 1, tmpvalue);
							} else {
								if (parameterTypeName
										.equals("java.sql.Timestamp")) {
									cstmt
											.setTimestamp(
													i + 1,
													DateUtil
															.getTimestampAndTimeFromString(param[j]));
								} else {
									if (parameterTypeName
											.equals("java.sql.Date")) {
										cstmt.setDate(i + 1, DateUtil
												.getDateFromString(param[j]));
									}
								}
							}
						}
					}
				}
			}
			cstmt.registerOutParameter(cnt, OracleTypes.CURSOR);
			cstmt.execute();
			rs = (ResultSet) cstmt.getObject(cnt);
			List ls = ConvertResultSetToList(rs);
			return ls;
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					cstmt = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					cstmt = null;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (open && myconn != null) {
				if (!getLongtran()) {
					close();
				}
			}
		}
	}

	/**
	 * ��������ִ�д洢��̣����������Ӳ��� wang ning 2008-2-25
	 * 
	 * @param functionname
	 * @param parameters
	 * @param returntype
	 * @return
	 * @throws SQLException
	 * @throws TxnException
	 * @throws Exception
	 */
	public Object execProcedureInTrans(String functionname,
			Object[][] parameters, int returntype) throws Exception {
		logger.info("execProcedureInTrans:" + functionname);
		if (getLongtran() && islongtransuccess == false)
			return null;
		myconn = getMYConnection();
		if (myconn == null) {
			throw new Exception("����δ����");
		}
		Object returnvalue = null;
		ResultSet cursor = null;
		StringBuffer sqlbuffer = new StringBuffer("");
		if (parameters == null) {
			if (returntype == OracleTypes.NULL) {
				sqlbuffer.append("{call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("()}");
			} else {
				sqlbuffer.append("{?=call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("()}");
			}
		} else {
			if (returntype == OracleTypes.NULL) {
				sqlbuffer.append("{call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("(");
				for (int i = 0; i < parameters.length; i++) {
					sqlbuffer.append("?,");
				}
				if (parameters.length > 0) {
					sqlbuffer
							.delete(sqlbuffer.length() - 1, sqlbuffer.length());
				}
				sqlbuffer.append(")}");
			} else {
				sqlbuffer.append("{?=call ");
				sqlbuffer.append(functionname);
				sqlbuffer.append("(");
				for (int i = 0; i < parameters.length; i++) {
					sqlbuffer.append("?,");
				}
				if (parameters.length > 0) {
					sqlbuffer
							.delete(sqlbuffer.length() - 1, sqlbuffer.length());
				}
				sqlbuffer.append(")}");
			}
		}
		String sql = sqlbuffer.toString();
		java.sql.CallableStatement callablestatement = null;
		try {
			try {
				callablestatement = myconn.getConnection().prepareCall(sql,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				if (returntype != OracleTypes.NULL) {
					callablestatement.registerOutParameter(1, returntype);
				}
				if (callablestatement == null)
					throw new Exception("��ִ��execFunctionʱprepareCall()����NULL!");
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							Class parameterTypeClass = parameters[j][1]
									.getClass();
							String parameterTypeName = parameterTypeClass
									.getName(); // ����ֵ������Class
							if (parameterTypeName.equals("java.lang.Integer")
									|| parameterTypeName.equals("int")) {
								if (returntype == OracleTypes.NULL) { // �޷���ֵ
									callablestatement.registerOutParameter(i,
											Types.INTEGER);
								} else {
									callablestatement.registerOutParameter(
											i + 1, Types.INTEGER);
								}
							} else {
								if (parameterTypeName
										.equalsIgnoreCase("java.lang.Float")) {
									if (returntype == OracleTypes.NULL) { // �޷���ֵ
										callablestatement.registerOutParameter(
												i, Types.FLOAT);
									} else {
										callablestatement.registerOutParameter(
												i + 1, Types.FLOAT);
									}
								} else {
									if (parameterTypeName
											.equals("java.lang.String")) {
										if (returntype == OracleTypes.NULL) { // �޷���ֵ
											callablestatement
													.registerOutParameter(i,
															Types.VARCHAR);
										} else {
											callablestatement
													.registerOutParameter(
															i + 1,
															Types.VARCHAR);
										}
									} else {
										if (parameterTypeName
												.equals("java.sql.Timestamp")) {
											if (returntype == OracleTypes.NULL) { // �޷���ֵ
												callablestatement
														.registerOutParameter(
																i,
																Types.TIMESTAMP);
											} else {
												callablestatement
														.registerOutParameter(
																i + 1,
																Types.TIMESTAMP);
											}
										} else {
											if (parameterTypeName
													.equals("java.sql.Date")) {
												if (returntype == OracleTypes.NULL) { // �޷���ֵ
													callablestatement
															.registerOutParameter(
																	i,
																	Types.DATE);
												} else {
													callablestatement
															.registerOutParameter(
																	i + 1,
																	Types.DATE);
												}
											}
										}
									}
								}
							}
						} else {
							if (parameterdirection.equalsIgnoreCase("in")) // �������
							{
								Class parameterTypeClass = parameters[j][1]
										.getClass();
								String parameterTypeName = parameterTypeClass
										.getName(); // ����ֵ������Class
								if (parameterTypeName
										.equals("java.lang.Integer")
										|| parameterTypeName.equals("int")) {
									if (returntype == OracleTypes.NULL) { // �޷���ֵ
										callablestatement.setInt(i,
												((Integer) parameters[j][1])
														.intValue());
									} else {
										callablestatement.setInt(i + 1,
												((Integer) parameters[j][1])
														.intValue());
									}
								} else {
									if (parameterTypeName
											.equals("java.lang.Float")
											|| parameterTypeName
													.equals("float")) {
										if (returntype == OracleTypes.NULL) { // �޷���ֵ
											callablestatement.setFloat(i,
													((Float) parameters[j][1])
															.floatValue());
										} else {
											callablestatement.setFloat(i + 1,
													((Float) parameters[j][1])
															.floatValue());
										}
									} else {
										if (parameterTypeName
												.equals("java.lang.String")) {
											if (returntype == OracleTypes.NULL) { // �޷���ֵ
												String tmpvalue = "";
												if (parameters[j][1] != null) {
													tmpvalue = com.jl.util.StringUtils
															.replaceString(
																	parameters[j][1]
																			.toString(),
																	"'", "\"");
												}
												callablestatement.setString(i,
														tmpvalue);
											} else {
												String tmpvalue = "";
												if (parameters[j][1] != null) {
													tmpvalue = com.jl.util.StringUtils
															.replaceString(
																	parameters[j][1]
																			.toString(),
																	"'", "\"");
												}
												callablestatement.setString(
														i + 1, tmpvalue);
											}
										} else {
											if (parameterTypeName
													.equals("java.sql.Timestamp")) {
												if (returntype == OracleTypes.NULL) { // �޷���ֵ
													callablestatement
															.setTimestamp(
																	i,
																	(java.sql.Timestamp) parameters[j][1]);
												} else {
													callablestatement
															.setTimestamp(
																	i + 1,
																	(java.sql.Timestamp) parameters[j][1]);
												}
											} else {
												if (parameterTypeName
														.equals("java.sql.Date")) {
													if (returntype == OracleTypes.NULL) { // �޷���ֵ
														callablestatement
																.setDate(
																		i,
																		(java.sql.Date) parameters[j][1]);
													} else {
														callablestatement
																.setDate(
																		i + 1,
																		(java.sql.Date) parameters[j][1]);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				callablestatement.execute();
				if (returntype != OracleTypes.NULL) {
					if (returntype != OracleTypes.CURSOR) {
						returnvalue = callablestatement.getObject(1); // �õ�����ֵ
					} else {
						cursor = ((OracleCallableStatement) callablestatement)
								.getCursor(1);
						List ls = ConvertResultSetToList(cursor);
						returnvalue = ls;
					}
				}
				// ����out��ֵ
				if (parameters != null) {
					for (int i = 1, j = 0; i <= parameters.length; i++, j++) {
						String parameterdirection = (String) parameters[j][0];
						if (parameterdirection.equalsIgnoreCase("out")) // �������
						{
							if (returntype != OracleTypes.NULL)
								parameters[j][1] = callablestatement
										.getObject(i + 1);
							else
								parameters[j][1] = callablestatement
										.getObject(i);
						}
					}
				}
			} finally {
				if (callablestatement != null) {
					callablestatement.close();
					callablestatement = null;
				}
			}
		} catch (SQLException ex) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					returnvalue = null;
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (Exception exception) {
			if (open && myconn != null) {
				if (!getLongtran()) {
					myconn.getConnection().rollback();
					returnvalue = null;
					// TransactionScope.setRollbackOnly();
				} else {
					islongtransuccess = false;
				}
			}
			logger.error(exception.getMessage());
			exception.printStackTrace();
			throw new Exception(exception.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return returnvalue;
	}

	/**
	 * ��ѯ���ת��ΪList(ͨ���к�) 2008-3-18
	 * 
	 * @param rs
	 * @return list
	 * @throws SQLException
	 */
	private List ConvertRstToListByNum(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = null;
		List rows = new ArrayList();
		if (rs != null) {
			rsmd = rs.getMetaData();
			// rs.beforeFirst();
			while (rs.next()) {
				Hashtable rowhashtable = new Hashtable();
				String counts = "";
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					counts = String.valueOf(i);
					String columnName = rsmd.getColumnName(i + 1).toLowerCase();
					switch (rsmd.getColumnType(i + 1)) {
					case Types.NUMERIC:
						if (rs.getString(columnName) == null)
							rowhashtable.put(counts, "");
						else
							rowhashtable.put(counts, rs.getString(columnName));
						break;
					case Types.VARCHAR:
						rowhashtable.put(counts,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName));
						break;
					case Types.INTEGER:
						rowhashtable.put(counts,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName));
						break;
					case Types.DATE: {
						java.sql.Date date = rs.getDate(columnName);
						if (date == null)
							rowhashtable.put(counts, "");
						else
							rowhashtable.put(counts, rs.getDate(columnName));
						break;
					}
					case Types.TIMESTAMP: {
						java.sql.Timestamp timestamp = rs
								.getTimestamp(columnName);
						if (timestamp == null)
							rowhashtable.put(counts, "");
						else
							rowhashtable.put(counts, rs
									.getTimestamp(columnName));
						break;
					}
					case Types.BLOB: {
						//oracle.sql.BLOB blobdata =(oracle.sql.BLOB)rs.getBlob(columnName);
						Blob blobdata = rs.getBlob(columnName);
						if (blobdata == null)
							rowhashtable.put(counts, "");
						else
							rowhashtable.put(counts, StringUtils
									.getStringFromBlob(blobdata));
						break;
					}
					case Types.CLOB: {
						Clob blobdata = rs.getClob(columnName);
						if (blobdata == null)
							rowhashtable.put(counts, "");
						else
							rowhashtable.put(counts, blobdata);
						break;
					}
					case Types.LONGVARCHAR: {
						java.io.Reader long_out = rs
								.getCharacterStream(columnName);
						if (long_out != null) {
							char[] long_buf = new char[8192];
							StringBuffer buffer = new StringBuffer();
							int len = 0;
							try {
								while ((len = long_out.read(long_buf)) > 0) {
									buffer.append(long_buf, 0, len);
								}
								rowhashtable.put(counts, buffer.toString());
								buffer = null;
							} catch (IOException e) {
								throw new SQLException(e.getMessage());
							} finally {
								long_buf = null;
							}
						}
						break;
					}
					default: {
						rowhashtable.put(counts,
								rs.getString(columnName) == null ? "" : rs
										.getString(columnName));
						break;
					}
					}
				}
				rows.add((Object) rowhashtable);
			}
		}
		return rows;
	}

	/**
	 * ��ѯ����������List����ͨ���к�ȡֵ
	 */
	public List openSelectbyNum(String s) throws Exception, SQLException {
		List ls = null;
		logger.info("openSelectbyNum:" + s);
		if (s == null)
			throw new Exception("Query Sql is Empty!");
		try {
			if (!open) // û�д�����
			{
				if (myconn == null) {
					myconn = getMYConnection();
					myconn.getConnection().setAutoCommit(false);
				}
				open = true;
			}
			if (myconn == null)
				throw new Exception("DataBase Connection is NULL");
			try {
				stmt = myconn.getConnection().prepareStatement(s);
				if (stmt == null)
					throw new Exception("stmt is null");
				try {
					rset = stmt.executeQuery();
					if (rset == null)
						throw new Exception("ResultSet is null");
					ls = ConvertRstToListByNum(rset);
					return ls;
				} finally {
					if (rset != null) {
						rset.close();
						rset = null;
					}
				}
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				close();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			logger.error(s);
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * ִ��һ�� sql ��䣬���ص�һ�е�һ��ֵ (Ϊ��ʱ����null)
	 * 
	 * @param sql
	 * @return
	 */
	public Object executeScalar(String sql) throws Exception {
		logger.info("executeScalar:" + sql);
		Object rtnValue = null;
		String _sql = "select * from (" + sql + ") where rownum=1 ";
		List list = null;
		Hashtable ht = new Hashtable();
		try {
			list = openSelectbyNum(_sql);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} finally {
			close();
		}
		if (list != null && list.size() > 0) {
			ht = (Hashtable) list.get(0);// ȡ��һ��
			rtnValue = ht.get("0");// ȡ��һ�����
		}
		return rtnValue;
	}

	public boolean delete(Object bean) throws Exception {
		String class_table_name = null;
		String class_primary_name = null;
		String class_schema_name = null;
		Object object = RefUtil.getFieldValue(bean, "class_tablename");
		if (object != null)
			class_table_name = (String) object;
		object = RefUtil.getFieldValue(bean, "class_primary");
		if (object != null)
			class_primary_name = (String) object;
		object = RefUtil.getFieldValue(bean, "class_schema");
		if (object != null)
			class_schema_name = (String) object;
		object = RefUtil.getFieldValue(bean, "class_update_fields");
		if (class_table_name != null && class_primary_name != null
				&& class_schema_name != null) {
			String fulltablename = class_schema_name.trim() + "."
					+ class_table_name.trim();
			fulltablename = fulltablename.toLowerCase();
			class_primary_name = class_primary_name.toLowerCase().trim();
			String bean_primary_value = null;
			object = RefUtil.getFieldValue(bean, class_primary_name);
			// String primarytype=RefUtil.getFieldType(bean,
			// class_primary_name);
			if (object == null)
				bean_primary_value = "";
			else {
				bean_primary_value = object.toString();
			}
			Object[] parameters = new Object[1];
			if (bean_primary_value.equalsIgnoreCase("") == false) {
				parameters[0] = bean_primary_value;
				// delete
				String[] tjvalues = new String[1];
				String[] tjnames = new String[1];
				tjvalues[0] = bean_primary_value;
				tjnames[0] = class_primary_name;
				delete(tjnames, tjvalues, fulltablename);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean save(Object bean) throws Exception {
		String class_table_name = null;
		String class_primary_name = null;
		String class_schema_name = null;
		String primary_seqname = null;

		// ֵ�����Ӧ�ĸ����ֶ�
		List class_update_fields = null;

		Object object = RefUtil.getFieldValue(bean, "class_tablename");
		if (object != null)
			class_table_name = (String) object;

		object = RefUtil.getFieldValue(bean, "class_primary");
		if (object != null)
			class_primary_name = (String) object;

		object = RefUtil.getFieldValue(bean, "class_schema");
		if (object != null)
			class_schema_name = (String) object;

		object = RefUtil.getFieldValue(bean, "class_update_fields");
		if (object != null)
			class_update_fields = (ArrayList) object;

		object = RefUtil.getFieldValue(bean, "primary_seqname");
		if (object != null)
			primary_seqname = (String) object;

		if (class_table_name != null && class_primary_name != null
				&& class_schema_name != null) {
			String fulltablename = class_schema_name.trim() + "."
					+ class_table_name.trim();
			fulltablename = fulltablename.toLowerCase();
			class_primary_name = class_primary_name.toLowerCase().trim();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ");
			sql.append(fulltablename);
			sql.append(" where ");
			sql.append(class_primary_name);
			sql.append("=?");
			Object bean_primary_value = null;
			bean_primary_value = RefUtil
					.getFieldValue(bean, class_primary_name);
			if (bean_primary_value == null)
				bean_primary_value = "";
			if (bean_primary_value.toString().equalsIgnoreCase("") == false
					&& bean_primary_value.toString().equalsIgnoreCase("0") == false) {
				Object[] parameters = new Object[1];
				parameters[0] = bean_primary_value;
				List ls = openSelectbyList(sql.toString(), parameters);
				if (class_update_fields != null) {
					if (class_update_fields.size() > 0) {
						// ����
					} else {
						// ��ȫ
						if (ls.size() > 0) {
							// update
							update(fulltablename, bean);
						} else {
							// insert
							insert(fulltablename, bean);
						}
					}
				} else {
					// ��ȫ
					if (ls.size() > 0) {
						// update
						update(fulltablename, bean);
					} else {
						// insert
						insert(fulltablename, bean);
					}
				}
			} else {
				Object refvalue = primary_seqname;
				if (refvalue != null) {
					String seqname = refvalue.toString();
					if (seqname.equalsIgnoreCase("") == false) {
						String seqsql = "select " + seqname
								+ ".nextval as primaryvalue from dual";
						List ls = this.openSelectbyList(seqsql);
						Hashtable ht = (Hashtable) ls.get(0);
						String primaryvalue = (String) ht.get("primaryvalue");
						if (RefUtil.setFieldValue(bean, class_primary_name,
								primaryvalue)) {
							insert(fulltablename, bean);
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
				return true;
			}
			return true;
		} else {
			return false;
		}
	}

	public List getBeansBySql(String[] param, String tablename,
			String conditions, Class beanclass) throws IllegalAccessException,
			InstantiationException, SQLException, Exception {
		List beanlist = new ArrayList();
		try {
			beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			throw ex1;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			throw ex1;
		}

		List ls = null;
		String sql = null;
		try {
			sql = RefUtil.getSQL(tablename, param, conditions, SqlType.Select);
			ls = openSelectbyList(sql);// logger.info(sql);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							Method sm = beanclass.getDeclaredMethod(
									setmethodname, paratype);
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("content")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {
								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
				throw ex3;
			} catch (Exception ex3) {
				ex3.printStackTrace();
				throw ex3;
			}

			ls = null;
		}
		return beanlist;
	}

	/**
	 * ͨ������ȡ�ֶ���Bean���Զ�Ӧ�Ĳ���(typeΪupdateʱ��ȡBean������ݿ��Ӧ�������ֶ�,Ϊinsertʱ���ȡ��Ӧ�Ҳ�Ϊ�յ��ֶ�)
	 * 
	 * @param tablename
	 *            String
	 * @param bean
	 *            Object
	 * @param type
	 *            String
	 * @return String[]
	 * @throws Exception
	 */
	private String[] getParamNames(String tablename, Object bean, String type)
			throws Exception {
		String[] colarr = null;
		List list = new ArrayList();
		if (tablename == null || tablename.trim().equals("")) {
			return null;
		}
		if (tablename.indexOf(".") == -1) {
			tablename = "cps." + tablename;
		}
		String sql = "select * from " + tablename + " where 1>2";

		try {
			String[] rm = getDBColNames(sql);
			colarr = RefUtil.getParamNames(bean);
			int cnt = 0;
			if (rm != null) {
				cnt = rm.length;
			}
			for (int i = 1; rm != null && i <= cnt; i++) {
				String colname = rm[i - 1]; // rm.getColumnName(i);
				for (int j = 0; j < colarr.length; j++) {
					if (colname.equalsIgnoreCase(colarr[j])) {
						list.add(colarr[j]);
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex2) {
				logger.error(ex2.getMessage());
				ex2.printStackTrace();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		int arrlength = 0;
		String[] returnarr;
		if (!type.equalsIgnoreCase("update")) {
			for (int i = 0; i < list.size(); i++) {
				String fieldname = list.get(i).toString();
				if (isNullNotSetValue(bean, fieldname)) {
					arrlength++;
				}
			}
			returnarr = new String[arrlength];
			for (int i = 0, j = 0; i < list.size() && j < returnarr.length; i++) {
				String fieldname = list.get(i).toString();
				if (isNullNotSetValue(bean, fieldname)) {
					returnarr[j] = fieldname;
					j++;
				}
			}
		} else {
			arrlength = list.size();
			returnarr = new String[arrlength];
			for (int i = 0; i < list.size(); i++) {
				String fieldname = list.get(i).toString();
				returnarr[i] = fieldname;
			}
		}
		return returnarr;
	}

	/**
	 * ȥ���ַ����һ���ַ�
	 * 
	 * @param srcStr
	 *            String
	 * @param c
	 *            String
	 * @return String
	 */
	private String trimChar(String srcStr, String c) {
		if (srcStr == null) {
			return null;
		}
		if (c == null) {
			return srcStr;
		}
		int index = srcStr.lastIndexOf(c);
		int length = srcStr.length();
		if (index == length - 1) {
			if (index != -1) {
				if (srcStr.length() > 1) {
					srcStr = srcStr.substring(0, index);
				} else {
					return "";
				}
			}
		}
		return srcStr;
	}

	/**
	 * ���Bean����ֵΪ�գ��򷵻�false,����Ϊtrue
	 * 
	 * @param bean
	 *            Object
	 * @param fieldname
	 *            String
	 * @return boolean
	 * @throws Exception
	 */
	private boolean isNullNotSetValue(Object bean, String fieldname)
			throws Exception {
		Object object = RefUtil.getFieldValue(bean, fieldname);
		if (object != null)
			return true;
		else
			return false;
	}

	/*
	 * public boolean insert(String tablename, Object bean, String conditions)
	 * throws Exception, SQLException { boolean result = false; String[] param =
	 * getParamNames(tablename, bean, "insert"); String sql = getSQL(tablename,
	 * param, conditions, SqlType.Insert); result = runSql(sql, param, bean);
	 * return result; }
	 */

	public boolean insert(String tablename, Object bean) throws Exception,
			SQLException {
		boolean result = false;
		String[] param = getParamNames(tablename, bean, "insert");
		// for(int i=0;i<param.length;i++){
		// logger.info("�浽��ݿ���ֶ�["+(i+1)+"]:"+param[i]);
		// }
		String sql = RefUtil.getSQL(tablename, param, "", SqlType.Insert);
		result = runSql(sql, param, bean);
		return result;
	}

	public boolean update(String tablename, Object bean, String conditions,
			String[] conditionvalues) throws Exception {
		boolean result = false;
		String[] beanparam = getParamNames(tablename, bean, "update");
		// for(int i=0;i<beanparam.length;i++){
		// logger.info("������ݿ���ֶ�["+(i+1)+"]:"+beanparam[i]);
		// }
		String[] param = new String[beanparam.length + conditionvalues.length];
		int j = 0;
		for (int i = 0; i < param.length; i++) {
			if (i < param.length - 1) {
				param[i] = beanparam[i];
			} else {
				param[i] = conditionvalues[j];
				j++;
			}
		}
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Update);
		// logger.info("SQL:"+sql);
		result = runSql(sql, param, bean);
		return result;
	}

	public boolean update(String tablename, Object bean) throws Exception,
			SQLException {
		boolean result = false;
		String[] beanparam = getParamNames(tablename, bean, "update");
		// for(int i=0;i<beanparam.length;i++){
		// logger.info("������ݿ���ֶ�["+(i+1)+"]:"+beanparam[i]);
		// }
		String class_primary_name = null;
		Object object = RefUtil.getFieldValue(bean, "class_primary");
		if (object != null)
			class_primary_name = (String) object;
		class_primary_name = class_primary_name.toLowerCase().trim();
		String tj = " where " + class_primary_name + "=?";
		String[] param = new String[beanparam.length - 1];
		int j = 0;
		for (int i = 0; i < beanparam.length; i++) {
			if (beanparam[i].equalsIgnoreCase(class_primary_name) == false) {
				param[j] = beanparam[i];
				j++;
			}
		}
		String[] updateparam = new String[beanparam.length];
		updateparam[beanparam.length - 1] = class_primary_name;
		j = 0;
		for (int i = 0; i < beanparam.length; i++) {
			if (beanparam[i].equalsIgnoreCase(class_primary_name) == false) {
				updateparam[j] = beanparam[i];
				j++;
			}
		}
		String sql = RefUtil.getSQL(tablename, param, tj, SqlType.Update);
		// logger.info("SQL:"+sql);
		result = runSql(sql, updateparam, bean);
		return result;
	}

	public Object initBean(String tablename, String conditions,
			Class beanclass, Object[] preparam) throws Exception {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, bean, "update");
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Select);

		List ls = null;
		try {
			ls = openSelectbyList(sql, preparam);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						{
							Hashtable ht = (Hashtable) obj;
							for (int i = 0; param != null && i < param.length; i++) {
								String setmethodname = "set" + param[i];
								String fieldname = "";
								fieldname = RefUtil.getFieldName(beanclass,
										param[i]);
								if (fieldname.trim().equalsIgnoreCase("")) {
									continue;
								}
								Field field = null;
								try {
									field = beanclass
											.getDeclaredField(fieldname);
								} catch (Exception e) {
									field = beanclass.getSuperclass()
											.getDeclaredField(fieldname);
								}
								Class[] paratype = { field.getType() };
								String settypename = field.getType().getName();
								Method sm = null;
								try {
									// sm = beanclass.getDeclaredMethod(
									// setmethodname, paratype);
									java.beans.BeanInfo info = java.beans.Introspector
											.getBeanInfo(beanclass);
									// ��ȡ����������
									java.beans.PropertyDescriptor pd[] = info
											.getPropertyDescriptors();
									// ����ֵ�ķ���
									Method mSet = null;
									for (int s = 0; s < pd.length; s++) {
										if (pd[s].getName().equalsIgnoreCase(
												fieldname)) {
											mSet = pd[s].getWriteMethod();
											sm = mSet;
										}
									}
									if (sm == null)
										continue;
								} catch (Exception e) {
									sm = beanclass.getSuperclass()
											.getDeclaredMethod(setmethodname,
													paratype);
								}
								// String getmethodname="get"+param[i];
								// Method
								// gm=beanclass.getMethod(getmethodname,null);
								// Object returnparamvalue=gm.invoke(bean,null);
								// String
								// returnparamtype=returnparamvalue.getClass().toString();
								Object[] p = new Object[1];
								if (settypename.equalsIgnoreCase("int")) {
									int rs_value = Integer.parseInt(ht.get(
											param[i].toLowerCase()).toString());// rs.getInt(param[i]);
									p[0] = new Integer(rs_value);
								} else if (settypename
										.equalsIgnoreCase("float")) {
									float rs_value = Float.parseFloat(ht.get(
											param[i].toLowerCase()).toString());// rs.getFloat(param[i]);
									p[0] = new Float(rs_value);
								} else if (settypename
										.equalsIgnoreCase("double")) {
									double rs_value = Double.parseDouble(ht
											.get(param[i].toLowerCase())
											.toString());// rs.getDouble(param[i]);
									p[0] = new Double(rs_value);
								} else if (settypename
										.equalsIgnoreCase("java.lang.String")) {
									if (!param[i]
											.equalsIgnoreCase("contentclob")) {
										p[0] = ht.get(param[i].toLowerCase()) == null ? ""
												: ht
														.get(
																param[i]
																		.toLowerCase())
														.toString();// rs.getString(param[i])

									} else {

										CLOB clob = (CLOB) ht.get(param[i]
												.toLowerCase()); // ors.getCLOB(param[i]);
										p[0] = getStringFromClob(clob);
									}
								} else if (settypename
										.equalsIgnoreCase("java.util.Date")) {
									try {
										String datestr = ht.get(
												param[i].toLowerCase())
												.toString();
										if (datestr != null) {
											if (datestr.length() > 10) {
												java.sql.Timestamp rs_value = null;
												rs_value = DateUtil
														.getTimestampAndTimeFromString(ht
																.get(
																		param[i]
																				.toLowerCase())
																.toString());
												p[0] = rs_value;
											} else {
												java.util.Date rs_value = null;
												rs_value = DateUtil
														.getDateFromString(ht
																.get(
																		param[i]
																				.toLowerCase())
																.toString());
												p[0] = rs_value;
											}
										}
										// rs.getDate(param[i]);
									} catch (Exception ex) {
										ex.printStackTrace();
										p[0] = null;
									}
								} else if (settypename
										.equalsIgnoreCase("java.sql.Timestamp")) {
									java.sql.Timestamp rs_value = null;
									try {
										rs_value = DateUtil
												.getTimestampAndTimeFromString(ht
														.get(
																param[i]
																		.toLowerCase())
														.toString());// rs.getTimestamp(param[i]);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									p[0] = rs_value;
								} else if (settypename
										.equalsIgnoreCase("java.sql.Date")) {
									java.sql.Date rs_value = DateUtil
											.getDateAndTimeFromString(ht.get(
													param[i].toLowerCase())
													.toString());// rs.getDate(param[i]);
									p[0] = rs_value;
								} else if (settypename
										.equals("java.lang.Float")
										|| settypename.equals("float")) {
									String rs_value = ht.get(
											param[i].toLowerCase()).toString();
									if (rs_value != null
											&& !rs_value.equals("")) {
										p[0] = new Float(rs_value);
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("java.lang.Integer")
										|| settypename.equalsIgnoreCase("int")) {
									String rs_value = ht.get(
											param[i].toLowerCase()).toString();
									if (rs_value != null
											&& !rs_value.equals("")) {
										p[0] = new Integer(rs_value);
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("java.lang.Double")) {
									String rs_value = ht.get(
											param[i].toLowerCase()).toString();
									if (rs_value != null
											&& !rs_value.equals("")) {
										p[0] = new Double(rs_value);
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("java.lang.Long")
										|| settypename.equalsIgnoreCase("long")) {
									String rs_value = ht.get(
											param[i].toLowerCase()).toString();
									if (rs_value != null
											&& !rs_value.equals("")) {
										p[0] = new Long(rs_value);
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("oracle.sql.CLOB")) {
									oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
											.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
									if (cb != null) {
										p[0] = cb;
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("oracle.sql.BLOB")) {
									oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
											.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
									if (cb != null) {
										p[0] = cb;
									} else {
										continue;
									}
								} else {
									p[0] = ht.get(param[i].toLowerCase());// rs.getObject((i
									// +
									// 1));
								}
								sm.invoke(bean, p);
							}
							return bean;
						}
					}
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}
			ls = null;
		}
	}

	/**
	 * ����ݿ���ȡ��ݳ�ʼ��Bean
	 * 
	 * @param tablename
	 *            String
	 * @param conditions
	 *            String
	 * @param beanclass
	 *            Class
	 * @return Object
	 * @throws Exception
	 */
	public Object initBean(String tablename, String conditions, Class beanclass)
			throws Exception {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, bean, "update");
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Select);

		List ls = null;

		// ResultSetMetaData rm = null;
		try {

			ls = openSelectbyList(sql);
			// while (rs.next())
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						{
							Hashtable ht = (Hashtable) obj;
							for (int i = 0; param != null && i < param.length; i++) {
								String setmethodname = "set" + param[i];
								String fieldname = "";
								fieldname = RefUtil.getFieldName(beanclass,
										param[i]);
								if (fieldname.trim().equalsIgnoreCase("")) {
									continue;
								}
								Field field = null;
								try {
									field = beanclass
											.getDeclaredField(fieldname);
								} catch (Exception e) {
									field = beanclass.getSuperclass()
											.getDeclaredField(fieldname);
								}
								Class[] paratype = { field.getType() };
								String settypename = field.getType().getName();
								Method sm = null;
								try {

									java.beans.BeanInfo info = java.beans.Introspector
											.getBeanInfo(beanclass);
									// ��ȡ����������
									java.beans.PropertyDescriptor pd[] = info
											.getPropertyDescriptors();
									// ����ֵ�ķ���
									Method mSet = null;
									for (int s = 0; s < pd.length; s++) {
										if (pd[s].getName().equalsIgnoreCase(
												fieldname)) {
											mSet = pd[s].getWriteMethod();
											sm = mSet;
										}
									}
									if (sm == null)
										continue;
									// sm = beanclass.getDeclaredMethod(
									// setmethodname, paratype);
								} catch (Exception e) {
									sm = beanclass.getSuperclass()
											.getDeclaredMethod(setmethodname,
													paratype);
								}
								// String getmethodname="get"+param[i];
								// Method
								// gm=beanclass.getMethod(getmethodname,null);
								// Object returnparamvalue=gm.invoke(bean,null);
								// String
								// returnparamtype=returnparamvalue.getClass().toString();
								// logger.info(fieldname+"_"+settypename+"_"+ht.get(param[i].toLowerCase()).toString());
								Object[] p = new Object[1];
								if (settypename.equalsIgnoreCase("int")) {
									if (ht.get(param[i].toLowerCase())
											.toString().equals("") == false) {
										int rs_value = Integer.parseInt(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getInt(param[i]);
										p[0] = new Integer(rs_value);
									} else {
										p[0] = new Integer(0);
									}

								} else if (settypename
										.equalsIgnoreCase("float")) {
									if (ht.get(param[i].toLowerCase())
											.toString().equals("") == false) {
										float rs_value = Float.parseFloat(ht
												.get(param[i].toLowerCase())
												.toString());// rs.getFloat(param[i]);
										p[0] = new Float(rs_value);
									} else {
										p[0] = new Float(0);
									}
								} else if (settypename
										.equalsIgnoreCase("double")) {
									if (ht.get(param[i].toLowerCase())
											.toString().equals("") == false) {
										double rs_value = Double.parseDouble(ht
												.get(param[i].toLowerCase())
												.toString());// rs.getDouble(param[i]);
										p[0] = new Double(rs_value);
									} else {
										p[0] = new Double(0);
									}
								} else if (settypename
										.equalsIgnoreCase("java.lang.String")) {
									if (!param[i]
											.equalsIgnoreCase("contentclob")) {
										p[0] = ht.get(param[i].toLowerCase()) == null ? ""
												: ht
														.get(
																param[i]
																		.toLowerCase())
														.toString();// rs.getString(param[i])

									} else {

										CLOB clob = (CLOB) ht.get(param[i]
												.toLowerCase()); // ors.getCLOB(param[i]);
										p[0] = getStringFromClob(clob);
									}
								} else if (settypename
										.equalsIgnoreCase("java.util.Date")) {
									try {
										String datestr = ht.get(
												param[i].toLowerCase())
												.toString();
										if (datestr != null) {
											if (datestr.length() > 10) {
												java.sql.Timestamp rs_value = null;
												rs_value = DateUtil
														.getTimestampAndTimeFromString(ht
																.get(
																		param[i]
																				.toLowerCase())
																.toString());
												p[0] = rs_value;
											} else {
												java.util.Date rs_value = null;
												rs_value = DateUtil
														.getDateFromString(ht
																.get(
																		param[i]
																				.toLowerCase())
																.toString());
												p[0] = rs_value;
											}
										}
										// rs.getDate(param[i]);
									} catch (Exception ex) {
										ex.printStackTrace();
										p[0] = null;
									}
								} else if (settypename
										.equalsIgnoreCase("java.sql.Timestamp")) {
									java.sql.Timestamp rs_value = null;
									try {
										rs_value = DateUtil
												.getTimestampAndTimeFromString(ht
														.get(
																param[i]
																		.toLowerCase())
														.toString());// rs.getTimestamp(param[i]);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									p[0] = rs_value;
								} else if (settypename
										.equalsIgnoreCase("java.sql.Date")) {
									java.sql.Date rs_value = DateUtil
											.getDateAndTimeFromString(ht.get(
													param[i].toLowerCase())
													.toString());// rs.getDate(param[i]);
									p[0] = rs_value;
								} else if (settypename
										.equals("java.lang.Float")
										|| settypename.equals("float")) {
									String rs_value = ht.get(
											param[i].toLowerCase()).toString();
									if (rs_value != null
											&& !rs_value.equals("")) {
										p[0] = new Float(rs_value);
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("java.lang.Integer")
										|| settypename.equalsIgnoreCase("int")) {
									String rs_value = ht.get(
											param[i].toLowerCase()).toString();
									if (rs_value != null
											&& !rs_value.equals("")) {
										p[0] = new Integer(rs_value);
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("oracle.sql.CLOB")) {
									oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
											.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
									if (cb != null) {
										p[0] = cb;
									} else {
										continue;
									}
								} else if (settypename
										.equalsIgnoreCase("oracle.sql.BLOB")) {
									oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
											.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
									if (cb != null) {
										p[0] = cb;
									} else {
										continue;
									}
								} else {
									p[0] = ht.get(param[i].toLowerCase());// rs.getObject((i
									// +
									// 1));
								}
								sm.invoke(bean, p);
							}
							return bean;
						}
					}
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
	}

	// ------------------

	/**
	 * ����ݿ���ȡ��ݳ�ʼ��Bean
	 * 
	 * @param tablename
	 *            String
	 * @param sql
	 *            String
	 * @param beanclass
	 *            Class
	 * @return Object
	 * @throws Exception
	 */
	public Object initBeanBySql(String tablename, String sql, Class beanclass)
			throws Exception {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, bean, "update");

		List ls = null;
		try {

			ls = openSelectbyList(sql);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						for (int i = 0; param != null && i < param.length; i++) {
							String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							// logger.info(fieldname + "�����ͣ�" +
							// settypename);
							Method sm = beanclass.getDeclaredMethod(
									setmethodname, paratype);
							// String getmethodname="get"+param[i];
							// Method
							// gm=beanclass.getMethod(getmethodname,null);
							// Object returnparamvalue=gm.invoke(bean,null);
							// String
							// returnparamtype=returnparamvalue.getClass().toString();
							Object[] p = new Object[1];
							// logger.info("settypename-->"+settypename+":fieldname-->"+fieldname);
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());// rs.getInt(param[i]);
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());// rs.getFloat(param[i]);
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());// rs.getDouble(param[i]);
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("contentclob")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();

								} else {
									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								java.sql.Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getTimestamp(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {
								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());// rs.getObject((i
								// +
								// 1));
								// logger.info( p[0]);
							}
							sm.invoke(bean, p);
						}
						return bean;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
	}

	public Object initBeanBySql(String tablename, String sql, Class beanclass,
			Object[] preparam) throws Exception {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, bean, "update");

		List ls = null;
		try {

			ls = openSelectbyList(sql, preparam);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							// logger.info(fieldname + "�����ͣ�" +
							// settypename);
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							// Method sm = beanclass.getDeclaredMethod(
							// setmethodname, paratype);
							// String getmethodname="get"+param[i];
							// Method
							// gm=beanclass.getMethod(getmethodname,null);
							// Object returnparamvalue=gm.invoke(bean,null);
							// String
							// returnparamtype=returnparamvalue.getClass().toString();
							Object[] p = new Object[1];
							// logger.info("settypename-->"+settypename+":fieldname-->"+fieldname);
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());// rs.getInt(param[i]);
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());// rs.getFloat(param[i]);
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());// rs.getDouble(param[i]);
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("contentclob")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();

								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								java.sql.Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getTimestamp(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {
								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());// rs.getObject((i
								// +
								// 1));
								// logger.info( p[0]);
							}
							sm.invoke(bean, p);
						}
						return bean;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
	}

	/**
	 * ����ݿ���ȡ��ݳ�ʼ��Bean
	 * 
	 * @param paramnames
	 *            String[]
	 * @param sql
	 *            String
	 * @param beanclass
	 *            Class
	 * @return Object
	 */
	public Object initBeanBySql(String[] paramnames, String sql, Class beanclass) {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = paramnames;

		List ls = null;
		try {

			ls = openSelectbyList(sql);// logger.info(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							// logger.info(fieldname + "�����ͣ�" +
							// settypename);
							// Method sm = beanclass.getDeclaredMethod(
							// setmethodname, paratype);
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							// String getmethodname="get"+param[i];
							// Method
							// gm=beanclass.getMethod(getmethodname,null);
							// Object returnparamvalue=gm.invoke(bean,null);
							// String
							// returnparamtype=returnparamvalue.getClass().toString();
							Object[] p = new Object[1];
							// logger.info("settypename-->"+settypename+":fieldname-->"+fieldname);
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());// rs.getInt(param[i]);
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());// rs.getFloat(param[i]);
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());// rs.getDouble(param[i]);
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("contentclob")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();

								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								java.sql.Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getTimestamp(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {
								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());// rs.getObject((i
								// +
								// 1));
								// logger.info( p[0]);
							}
							sm.invoke(bean, p);
						}
						return bean;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
	}

	/**
	 * ����ݿ���ȡ��ݳ�ʼ��Bean
	 * 
	 * @param sql
	 *            String
	 * @param beanclass
	 *            Class
	 * @return Object
	 */
	public Object initBeanBySql(String sql, Class beanclass) {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}

		List ls = null;
		try {

			ls = openSelectbyList(sql);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Method[] ms = beanclass.getSuperclass().getMethods();
						String fliedName = "";
						String parameterTypeName = "";
						for (int i = 0; i < ms.length; i++) {
							ms[i].getReturnType().equals(
									(new Integer(0)).getClass());
							if (ms[i].getName().substring(0, 3).equals("set")) {
								fliedName = ms[i].getName().substring(3);
								if (ht.containsKey(fliedName.toLowerCase())) {
									parameterTypeName = ms[i]
											.getParameterTypes()[0].getName();
									if (parameterTypeName
											.equals("java.lang.Integer")
											|| parameterTypeName.equals("int")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Integer(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Long")
											|| parameterTypeName.equals("long")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Long(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Float")
											|| parameterTypeName
													.equals("float")
											|| parameterTypeName
													.equals("float")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Float(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Double")
											|| parameterTypeName
													.equals("double")
											|| parameterTypeName
													.equals("double")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Double(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.lang.String"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { (String) ht
																.get(fliedName
																		.toLowerCase()) });
									} else if (parameterTypeName
											.equals(("java.sql.Date"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { java.sql.Date
																.valueOf((String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.sql.Timestamp"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { java.sql.Timestamp
																.valueOf((String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.util.Date"))) {
										java.util.Date rs_value = DateUtil
												.getDateFromString(ht
														.get(
																fliedName
																		.toLowerCase())
														.toString());// rs.getDate(param[i]);
										ms[i].invoke(bean,
												new Object[] { rs_value });
									}
								}
							}
						}
						ms = beanclass.getMethods();
						for (int i = 0; i < ms.length; i++) {
							ms[i].getReturnType().equals(
									(new Integer(0)).getClass());
							if (ms[i].getName().substring(0, 3).equals("set")) {
								fliedName = ms[i].getName().substring(3);
								if (ht.containsKey(fliedName.toLowerCase())) {
									parameterTypeName = ms[i]
											.getParameterTypes()[0].getName();
									if (parameterTypeName
											.equals("java.lang.Integer")
											|| parameterTypeName.equals("int")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Integer(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Long")
											|| parameterTypeName.equals("long")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Long(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Float")
											|| parameterTypeName
													.equals("float")
											|| parameterTypeName
													.equals("float")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Float(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Double")
											|| parameterTypeName
													.equals("double")
											|| parameterTypeName
													.equals("double")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Double(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.lang.String"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { (String) ht
																.get(fliedName
																		.toLowerCase()) });
									} else if (parameterTypeName
											.equals(("java.sql.Date"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { java.sql.Date
																.valueOf((String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.sql.Timestamp"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { java.sql.Timestamp
																.valueOf((String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.util.Date"))) {
										java.util.Date rs_value = DateUtil
												.getDateFromString(ht
														.get(
																fliedName
																		.toLowerCase())
														.toString());// rs.getDate(param[i]);
										ms[i].invoke(bean,
												new Object[] { rs_value });
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

		}
		return bean;
	}

	/**
	 * ����ݿ���ȡ��ݳ�ʼ��Bean
	 * 
	 * @param sql
	 *            String
	 * @param parameters
	 *            Object[]
	 * @param beanclass
	 *            Class
	 * @return Object
	 */
	public Object initBeanBySql(String sql, Object[] parameters, Class beanclass) {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}

		List ls = null;
		try {

			ls = openSelectbyList(sql, parameters);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;

						Method[] ms = beanclass.getMethods();
						String fliedName = "";
						String parameterTypeName = "";
						for (int i = 0; i < ms.length; i++) {
							ms[i].getReturnType().equals(
									(new Integer(0)).getClass());
							if (ms[i].getName().substring(0, 3).equals("set")) {
								fliedName = ms[i].getName().substring(3);
								if (ht.containsKey(fliedName.toLowerCase())) {
									parameterTypeName = ms[i]
											.getParameterTypes()[0].getName();
									if (parameterTypeName
											.equals("java.lang.Integer")
											|| parameterTypeName.equals("int")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Integer(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Long")
											|| parameterTypeName.equals("long")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Long(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Float")
											|| parameterTypeName
													.equals("float")
											|| parameterTypeName
													.equals("float")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Float(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals("java.lang.Double")
											|| parameterTypeName
													.equals("double")
											|| parameterTypeName
													.equals("double")) {
										ms[i]
												.invoke(
														bean,
														new Object[] { new Double(
																(String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.lang.String"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { (String) ht
																.get(fliedName
																		.toLowerCase()) });
									} else if (parameterTypeName
											.equalsIgnoreCase("java.util.Date")) {

										String datestr = ht.get(
												fliedName.toLowerCase())
												.toString();
										if (datestr != null) {
											if (datestr.length() > 10) {
												java.sql.Timestamp rs_value = null;
												rs_value = DateUtil
														.getTimestampAndTimeFromString(datestr);
												ms[i]
														.invoke(
																bean,
																new Object[] { rs_value });
											} else {
												java.util.Date rs_value = null;
												rs_value = DateUtil
														.getDateFromString(datestr);
												ms[i]
														.invoke(
																bean,
																new Object[] { rs_value });
											}
										}

									} else if (parameterTypeName
											.equals(("java.sql.Date"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { java.sql.Date
																.valueOf((String) ht
																		.get(fliedName
																				.toLowerCase())) });
									} else if (parameterTypeName
											.equals(("java.sql.Timestamp"))) {
										ms[i]
												.invoke(
														bean,
														new Object[] { java.sql.Timestamp
																.valueOf((String) ht
																		.get(fliedName
																				.toLowerCase())) });
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

		}
		return bean;
	}

	/**
	 * ����ݿ���ȡ��ݳ�ʼ���Ѵ��ڵ�Bean
	 * 
	 * @param tablename
	 *            String
	 * @param conditions
	 *            String
	 * @param bean
	 *            Object
	 * @return Object
	 * @throws Exception
	 */
	public Object initBean(String tablename, String conditions, Object bean)
			throws Exception {
		if (bean == null) {
			return null;
		}
		String[] param = getParamNames(tablename, bean, "update");
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Select);

		List ls = null;
		Class beanclass = bean.getClass();
		try {

			ls = openSelectbyList(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							/*
							 * Method sm = beanclass.getDeclaredMethod(
							 * setmethodname, paratype);
							 */
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());// rs.getInt(param[i]);
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());//
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());//
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("contentclob")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();

								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								try {
									String datestr = ht.get(
											param[i].toLowerCase()).toString();
									if (datestr != null) {
										if (datestr.length() > 10) {
											java.sql.Timestamp rs_value = null;
											rs_value = DateUtil
													.getTimestampAndTimeFromString(ht
															.get(
																	param[i]
																			.toLowerCase())
															.toString());
											p[0] = rs_value;
										} else {
											java.util.Date rs_value = null;
											rs_value = DateUtil
													.getDateFromString(ht
															.get(
																	param[i]
																			.toLowerCase())
															.toString());
											p[0] = rs_value;
										}
									}
									// rs.getDate(param[i]);
								} catch (Exception ex) {
									ex.printStackTrace();
									p[0] = null;
								}
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {
								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());// rs.getObject((i
								// +
								// 1));
							}
							sm.invoke(bean, p);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;

		}
		return bean;
	}

	/**
	 * ����ݿ���ȡ��ݳ�ʼ��Bean
	 * 
	 * @param tablename
	 *            String
	 * @param conditions
	 *            String
	 * @param beanclass
	 *            Class
	 * @return Object
	 * @throws Exception
	 */
	public Object initBean_clob(String tablename, String conditions,
			Class beanclass) throws Exception {
		Object bean = null;
		try {
			bean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, bean, "update");
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Select);

		List ls = null;
		try {

			ls = openSelectbyList(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							// logger.info("setmethodname-->"+setmethodname);
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							/*
							 * Method sm = beanclass.getDeclaredMethod(
							 * setmethodname, paratype);
							 */
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								// if (!param[i].equalsIgnoreCase("content"))
								if (param[i].toLowerCase().indexOf("content") == -1
										|| param[i].toLowerCase().indexOf(
												"add_content") != -1) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {

									// rs;

									if (ht.get(param[i].toLowerCase()) != null
											&& !ht.get(param[i].toLowerCase())
													.equals("")) {
										CLOB clob = (CLOB) ht.get(param[i]
												.toLowerCase());// ors.getCLOB(param[i]);
										// logger.info(clob);
										p[0] = getStringFromClob(clob);// .replaceAll("\n\g",
										// "<br/>");
										// logger.info(p[0]);
									} else {
										p[0] = "";
									}
								}
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								if (ht.get(param[i].toLowerCase()) != null) {
									java.util.Date rs_value = DateUtil
											.getDateFromString(ht.get(
													param[i].toLowerCase())
													.toString());
									p[0] = rs_value;
								} else {
									p[0] = null;
								}
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								if (ht.get(param[i].toLowerCase()) != null) {
									java.sql.Date rs_value = DateUtil
											.getDateAndTimeFromString(ht.get(
													param[i].toLowerCase())
													.toString());
									p[0] = rs_value;
								} else {
									p[0] = null;
								}
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {
								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}

							sm.invoke(bean, p);
							// logger.info("ok!!!-->"+p[0]);
						}
						return bean;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
		return null;
	}

	public List getBeansBySql(String[] param, String sql, Class beanclass) {
		List beanlist = new ArrayList();
		try {
			beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}

		List ls = null;
		try {

			ls = openSelectbyList(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							/*
							 * Method sm = beanclass.getDeclaredMethod(
							 * setmethodname, paratype);
							 */
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("content")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {

								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {

								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
		return beanlist;
	}

	public List getBeansBySql(String[] param, String sql, Class beanclass,
			Object[] preparam) throws IllegalAccessException,
			InstantiationException, SQLException, Exception {
		List beanlist = new ArrayList();
		try {
			beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			throw ex1;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			throw ex1;
		}

		List ls = null;
		try {
			ls = openSelectbyList(sql, preparam);// logger.info(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							/*
							 * Method sm = beanclass.getDeclaredMethod(
							 * setmethodname, paratype);
							 */
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("content")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {

								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {

								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
				throw ex3;
			} catch (Exception ex3) {
				ex3.printStackTrace();
				throw ex3;
			}

			ls = null;
		}
		return beanlist;
	}

	public List getBeansBySql(String tablename, String sql, Class beanclass)
			throws Exception {
		List beanlist = new ArrayList();
		Object parabean = null;
		try {
			parabean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, parabean, "update");

		List ls = null;
		try {

			ls = openSelectbyList(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							// Method sm = beanclass.getDeclaredMethod(
							// setmethodname, paratype);
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("content")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {

								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {

								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}

							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
		return beanlist;
	}

	public List getBeansBySql(String tablename, String sql, Class beanclass,
			Object[] preparam) throws Exception {
		List beanlist = new ArrayList();
		Object parabean = null;
		try {
			parabean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, parabean, "update");

		List ls = null;
		try {
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			ls = openSelectbyList(sql, preparam);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = { beanclass.getDeclaredField(
									fieldname).getType() };
							String settypename = beanclass.getDeclaredField(
									fieldname).getType().getName();
							// Method sm = beanclass.getDeclaredMethod(
							// setmethodname, paratype);
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								p[0] = ht.get(param[i].toLowerCase()) == null ? ""
										: ht.get(param[i].toLowerCase())
												.toString();
								/*
								 * if (!param[i].equalsIgnoreCase("content")) {
								 * p[0] = ht.get(param[i].toLowerCase()) == null ? "" :
								 * ht.get(param[i].toLowerCase()) .toString(); }
								 * else { CLOB clob = (CLOB) ht.get(param[i]
								 * .toLowerCase());// ors.getCLOB(param[i]);
								 * p[0] = getStringFromClob(clob); }
								 */
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Long")
									|| settypename.equalsIgnoreCase("long")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Long(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {

								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {

								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}

							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
		return beanlist;
	}

	public List getBeans(String tablename, String conditions, Class beanclass,
			Object[] preparam) throws Exception {
		List beanlist = new ArrayList();
		Object parabean = null;
		try {
			parabean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, parabean, "update");
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Select);

		List ls = null;
		try {
			ls = openSelectbyList(sql, preparam);// logger.info(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = new Class[1];
							try {
								paratype[0] = beanclass.getDeclaredField(
										fieldname).getType();
							} catch (Exception e) {
								paratype[0] = beanclass.getSuperclass()
										.getDeclaredField(fieldname).getType();
							}
							String settypename = "";
							try {
								settypename = beanclass.getDeclaredField(
										fieldname).getType().getName();
							} catch (Exception e) {
								settypename = beanclass.getSuperclass()
										.getDeclaredField(fieldname).getType()
										.getName();
							}
							/*
							 * Method sm = null; try { sm =
							 * beanclass.getDeclaredMethod(setmethodname,
							 * paratype); } catch (Exception e) { sm =
							 * beanclass.getSuperclass()
							 * .getDeclaredMethod(setmethodname, paratype); }
							 */
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("content")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {

								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
		return beanlist;
	}

	public List getBeans(String tablename, String conditions, Class beanclass)
			throws Exception {
		List beanlist = new ArrayList();
		Object parabean = null;
		try {
			parabean = beanclass.newInstance();
		} catch (IllegalAccessException ex1) {
			ex1.printStackTrace();
			return null;
		} catch (InstantiationException ex1) {
			ex1.printStackTrace();
			return null;
		}
		String[] param = getParamNames(tablename, parabean, "update");
		String sql = RefUtil.getSQL(tablename, param, conditions,
				SqlType.Select);

		List ls = null;
		try {
			ls = openSelectbyList(sql);// logger.info(sql);
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// ��ȡ����������
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();

			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						Object bean = beanclass.newInstance();
						for (int i = 0; param != null && i < param.length; i++) {
							// String setmethodname = "set" + param[i];
							String fieldname = "";
							fieldname = RefUtil.getFieldName(beanclass,
									param[i]);
							if (fieldname.trim().equalsIgnoreCase("")) {
								continue;
							}
							Class[] paratype = new Class[1];
							try {
								paratype[0] = beanclass.getDeclaredField(
										fieldname).getType();
							} catch (Exception e) {
								paratype[0] = beanclass.getSuperclass()
										.getDeclaredField(fieldname).getType();
							}
							String settypename = "";
							try {
								settypename = beanclass.getDeclaredField(
										fieldname).getType().getName();
							} catch (Exception e) {
								settypename = beanclass.getSuperclass()
										.getDeclaredField(fieldname).getType()
										.getName();
							}
							/*
							 * Method sm = null; try { sm =
							 * beanclass.getDeclaredMethod(setmethodname,
							 * paratype); } catch (Exception e) { sm =
							 * beanclass.getSuperclass()
							 * .getDeclaredMethod(setmethodname, paratype); }
							 */
							Method sm = null;
							for (int m = 0; m < pd.length; m++) {
								if (pd[m].getName().equalsIgnoreCase(fieldname)) {
									sm = pd[m].getWriteMethod();
									break;
								}
							}
							Object[] p = new Object[1];
							if (settypename.equalsIgnoreCase("int")) {
								int rs_value = Integer.parseInt(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Integer(rs_value);
							} else if (settypename.equalsIgnoreCase("float")) {
								float rs_value = Float.parseFloat(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Float(rs_value);
							} else if (settypename.equalsIgnoreCase("double")) {
								double rs_value = Double.parseDouble(ht.get(
										param[i].toLowerCase()).toString());
								p[0] = new Double(rs_value);
							} else if (settypename
									.equalsIgnoreCase("java.lang.String")) {
								if (!param[i].equalsIgnoreCase("content")) {
									p[0] = ht.get(param[i].toLowerCase()) == null ? ""
											: ht.get(param[i].toLowerCase())
													.toString();
								} else {

									// rs;
									CLOB clob = (CLOB) ht.get(param[i]
											.toLowerCase());// ors.getCLOB(param[i]);
									p[0] = getStringFromClob(clob);
								}
							} else if (settypename
									.equalsIgnoreCase("java.util.Date")) {
								java.util.Date rs_value = DateUtil
										.getDateFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Timestamp")) {
								Timestamp rs_value = DateUtil
										.getTimestampAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename
									.equalsIgnoreCase("java.sql.Date")) {
								java.sql.Date rs_value = DateUtil
										.getDateAndTimeFromString(ht.get(
												param[i].toLowerCase())
												.toString());// rs.getDate(param[i]);
								p[0] = rs_value;
							} else if (settypename.equals("java.lang.Float")
									|| settypename.equals("float")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Float(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("java.lang.Integer")
									|| settypename.equalsIgnoreCase("int")) {
								String rs_value = ht
										.get(param[i].toLowerCase()).toString();
								if (rs_value != null && !rs_value.equals("")) {
									p[0] = new Integer(rs_value);
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.CLOB")) {
								oracle.sql.CLOB cb = (oracle.sql.CLOB) ht
										.get(param[i].toLowerCase());// ors.getCLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else if (settypename
									.equalsIgnoreCase("oracle.sql.BLOB")) {

								oracle.sql.BLOB cb = (oracle.sql.BLOB) ht
										.get(param[i].toLowerCase());// ors.getBLOB(param[i]);
								if (cb != null) {
									p[0] = cb;
								} else {
									continue;
								}
							} else {
								p[0] = ht.get(param[i].toLowerCase());
							}
							sm.invoke(bean, p);
						}
						beanlist.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			try {
				close();
			} catch (SQLException ex3) {
				ex3.printStackTrace();
			} catch (Exception ex3) {
				ex3.printStackTrace();
			}

			ls = null;
		}
		return beanlist;
	}

	public String getStringFromClob(CLOB clob) {
		String result = "";
		try {
			java.io.Reader in = clob.getCharacterStream();
			if (in == null)
				return null;
			StringBuffer sb = new StringBuffer(2048);
			int i = in.read();
			while (i != -1) {
				sb.append((char) i);
				i = in.read();
			}
			in.close();
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param tablename
	 *            String[]
	 * @param conditions
	 *            String[]
	 * @return int
	 */
	public boolean delete(String[] tablename, String[] conditions)
			throws Exception {
		boolean result = false;
		if (tablename == null) {
			return false;
		}
		List sqls = new ArrayList();
		for (int i = 0; i < tablename.length; i++) {
			String singletablename = "";
			singletablename = tablename[i];
			if (singletablename.indexOf(".") == -1) {
				singletablename = "cps." + singletablename;
			}
			if (conditions != null) {
				if (i < conditions.length) {
					sqls.add("delete from " + singletablename + " "
							+ conditions[i]);
				} else {
					sqls.add("delete from " + singletablename);
				}
			} else {
				sqls.add("delete from " + singletablename);
			}
		}
		try {
			try {
				int ret = execBatchSql(sqls);
				result = (ret == 1 ? true : false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {

			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 
	 * @param ids
	 *            String[]
	 * @param tablename
	 *            String
	 * @return int
	 */
	public boolean delete(String[] tjnames, String[] tjvalues, String tablename)
			throws Exception {
		boolean result = false;
		if (tjvalues == null) {
			return false;
		}
		if (tjnames == null) {
			return false;
		}
		if (tjvalues.length != tjnames.length) {
			return false;
		}
		if (tablename.indexOf(".") == -1) {
			tablename = "cps." + tablename;
		}
		StringBuffer sql = new StringBuffer("delete from ");
		sql.append(tablename);
		if (tjvalues.length > 0)
			sql.append(" where ");
		for (int i = 0; i < tjvalues.length; i++) {
			if (i < tjvalues.length - 1) {
				sql.append(tjnames[i] + "=? and ");
			} else {
				sql.append(tjnames[i] + "=?");
			}
		}
		try {
			try {
				result = runSql(sql.toString(), tjvalues);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {

			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 
	 * 
	 * @param organid
	 *            String
	 * @param tagName
	 *            String
	 * @param funName
	 *            String
	 * @param width
	 *            String
	 * @return String
	 */
	public String getDeptNameList(String organid, String tagName,
			String funName, String width) {

		StringBuffer sql = new StringBuffer();
		StringBuffer returnvalue = new StringBuffer();
		organid = (organid == null || organid.trim().equals("") ? "-1"
				: organid);
		sql
				.append("select deptid,deptname from cps.v_b_deptinfo where DEPARTMENT_CLASS_CODE=1 and organid=");
		sql.append(organid);

		List ls = null;

		try {
			try {
				ls = openSelectbyList(sql.toString());// openSelect(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			tagName = (tagName == null || tagName.trim().equals("") ? "dept_name"
					: tagName);
			width = (width == null || width.trim().equals("") ? "100%" : width);
			if (funName != null && !funName.trim().equals("")) {
				returnvalue.append("<select name='" + tagName + "' id='"
						+ tagName + "' onchange='" + funName
						+ "' style='width:" + width + "'>");
			} else {
				returnvalue.append("<select name='" + tagName + "' id='"
						+ tagName + "' style='width:" + width + "'>");
			}
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						String deptname = ht.get("deptname") == null ? " " : ht
								.get("deptname").toString();
						returnvalue.append("<option value="
								+ Integer.parseInt(ht.get("deptid").toString())
								+ ">" + deptname + "</option>");
					}
				}
			}
			returnvalue.append("</select>");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				close();
			} catch (SQLException ex2) {
				ex2.printStackTrace();
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}

			ls = null;
		}
		return returnvalue.toString();
	}

	/**
	 * �����ͬʱ����ʱ�������(�����������е��������Ҫһ��)
	 * 
	 * @param tablename
	 *            String[] ����
	 * @param bean
	 *            Object[] ��ݶ���
	 * @param sqltype
	 *            SqlType[] �洢���ͣ�INSERT��UPDATE��
	 * @param conditions
	 *            String[] ��������
	 * @return int ����ֵ(�ɹ����أ�true��ʧ�ܷ��أ�false)
	 */
	public boolean saveBeans(String[] tablename, Object[] bean,
			String[] sqltype, String[] conditions) {
		boolean result = false;
		try {
			String[] sqls = new String[bean.length];
			String[][] params = new String[bean.length][];
			for (int x = 0; bean != null && tablename != null
					&& sqltype != null && x < tablename.length
					&& x < bean.length; x++) {
				String[] param = getParamNames(tablename[x], bean[x],
						sqltype[x]);
				String sql = RefUtil.getSQL(tablename[x], param,
						conditions[x] == null ? "" : conditions[x], sqltype[x]
								.toString());
				sqls[x] = sql;
				params[x] = param;

			}
			result = runSql(sqls, params, bean);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			ex.printStackTrace();
			/** @todo Handle this exception */
		}
		return result;
	}

	/**
	 * 
	 * @param tablename
	 *            String
	 * @param fieldname
	 *            String
	 * @param values
	 *            String
	 * @param date_fname
	 *            String
	 * @param date_value
	 *            String
	 * @param type
	 *            String
	 * @param mainid
	 *            String
	 * @return int
	 */
	public boolean common_operator(String tablename, String fieldname,
			String values, String date_fname, String date_value, String type,
			String mainid) throws Exception {
		boolean result = false;
		if (fieldname == null)
			return false;
		String[] fn = fieldname.split(",.,");
		String[] v = values.split(",.,");
		String sql = null;
		StringBuffer sb = new StringBuffer();
		if (type.equals("insert")) {
			sb.append("insert into " + tablename + "(");
			for (int i = 0; i < fn.length; i++) {
				if (i == fn.length - 1) {
					sb.append(fn[i]);
				} else {
					sb.append(fn[i] + ",");
				}
			}
			if (date_fname == null || date_fname.trim().equals("")) {
				sb.append(") values(");
				for (int i = 0; i < v.length; i++) {
					if (i == v.length - 1) {
						sb.append("'" + v[i] + "')");
					} else {
						sb.append("'" + v[i] + "',");
					}
				}
			} else {
				String[] df = date_fname.split(",.,");
				String[] dv = date_value.split(",.,");

				for (int j = 0; j < df.length; j++) {
					if (j == df.length - 1) {
						sb.append(df[j] + ") values(");
					} else {
						sb.append("," + df[j] + ",");
					}
				}
				for (int i = 0; i < v.length; i++) {
					sb.append("'" + v[i] + "',");
				}

				for (int j = 0; j < dv.length; j++) {
					if (j == dv.length - 1) {
						if (dv[j].length() < 10) {
							sb.append("to_date('" + dv[j] + "','yyyy-mm-dd'))");
						} else {
							sb.append("to_date('" + dv[j]
									+ "','yyyy-mm-dd hh24:mi:ss'))");
						}
					} else {
						if (dv[j].length() < 10) {
							sb.append("to_date('" + dv[j] + "','yyyy-mm-dd'),");
						} else {
							sb.append("to_date('" + dv[j]
									+ "','yyyy-mm-dd hh24:mi:ss'),");
						}
					}
				}
			}

		} else if (type.equals("update")) {
			sb.append("update " + tablename + " set ");
			if (date_fname == null || date_fname.trim().equals("")) {
				for (int i = 0; i < fn.length; i++) {
					// if(v[i].trim().equals("")){continue;}
					if (i == fn.length - 1) {
						sb.append(fn[i] + "='" + v[i] + "' ");
					} else {
						sb.append(fn[i] + "='" + v[i] + "',");
					}
				}
			} else {
				String[] df = date_fname.split(",.,");
				String[] dv = date_value.split(",.,");
				for (int i = 0; i < fn.length; i++) {
					// if(v[i].trim().equals("")){continue;}
					sb.append(fn[i] + "='" + v[i] + "',");
				}
				for (int j = 0; j < df.length; j++) {
					// if(dv[j].trim().equals("")){continue;}
					if (j == df.length - 1) {
						if (dv[j].length() < 10) {
							sb.append(df[j] + "=to_date('" + dv[j]
									+ "','yyyy-mm-dd')");
						} else {
							sb.append(df[j] + "=to_date('" + dv[j]
									+ "','yyyy-mm-dd hh24:mi:ss')");
						}
					} else {
						if (dv[j].length() < 10) {
							sb.append(df[j] + "=to_date('" + dv[j]
									+ "','yyyy-mm-dd'),");
						} else {
							sb.append(df[j] + "=to_date('" + dv[j]
									+ "','yyyy-mm-dd hh24:mi:ss'),");
						}
					}
				}
			}
			if (mainid != null)
				sb.append(" where mainid=" + mainid);
		}
		sql = sb.toString();

		List ls = null;
		try {

			if (type.equals("insert")) {
				String tmp_sql = "select count(*) cnt from " + tablename
						+ " where mainid=" + mainid;
				int cnt = 0;
				ls = openSelectbyList(tmp_sql);// openSelect(tmp_sql);
				if (ls != null) {
					for (int k = 0; k < ls.size(); k++) {
						Object obj = ls.get(k);
						if (obj instanceof Hashtable) {
							Hashtable ht = (Hashtable) obj;
							cnt = Integer.parseInt(ht.get("cnt").toString());
						}
					}
				}
				if (cnt == 0) {
					close();
					result = runSql(sql);
				} else {
					common_operator(tablename, fieldname, values, date_fname,
							date_value, "update", mainid);
				}
			} else {
				result = runSql(sql);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {

			try {
				close();
			} catch (SQLException ex1) {
				ex1.printStackTrace();
				throw ex1;
			}

			ls = null;
		}
		return result;
	}

	public int getCountNumber(String tablename, String condition)
			throws Exception {
		List ls = null;
		int cnt = 0;
		try {
			String tmp_sql = "select count(*) cnt from " + tablename + " "
					+ condition;

			ls = openSelectbyList(tmp_sql);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						Hashtable ht = (Hashtable) obj;
						cnt = Integer.parseInt(ht.get("cnt").toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			ls = null;
		}
		return cnt;
	}

	public int getCountNumber(String tmp_sql) throws Exception {

		List ls = null;
		int cnt = 0;
		try {
			ls = openSelectbyList(tmp_sql);
			if (ls != null) {
				for (int k = 0; k < ls.size(); k++) {
					Object obj = ls.get(k);
					if (obj instanceof Hashtable) {
						cnt = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			cnt = -1;
		} finally {

			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
				cnt = -1;
			}

			ls = null;
		}
		return cnt;
	}

	private String getRandomColor() {
		String mystr = "#"; // /���ص���ɫ����
		String[] str = { "0", "1", "2", "3", "5", "6", "7", "8", "9", "A", "B",
				"C", "D", "E", "F" };
		int i = 0;
		for (int k = 0; k < 6; k++) {
			Random r = new Random();
			i = r.nextInt(15);
			mystr += str[i];
		}
		return mystr;
	}

	public int genBeanClassFile(String tablename, String filepath,
			String filename, String packagename, int ishaveconstructor,
			String authorname) throws Exception, SQLException,
			NullPointerException, IOException, Exception {
		// xn08147528744
		if (tablename == null || tablename.trim().equals("")) {
			throw new NullPointerException("�������ͼ����Ϊ�գ�");
		}
		if (tablename.indexOf(".") == -1) {
			throw new SQLException("�����ͼû��ģʽ��");
		}
		String fullfilename = null;
		String parttablename = tablename.substring(tablename.indexOf(".") + 1);
		if (filename == null || filename.trim().equals("")) {
			if (tablename.length() > 1) {
				filename = parttablename.substring(0, 1).toUpperCase()
						+ parttablename.substring(1).toLowerCase();
			} else {
				filename = parttablename.substring(0, 1).toUpperCase();
			}
		} else {
			if (filename.length() > 1) {
				filename = filename.substring(0, 1).toUpperCase()
						+ filename.substring(1).toLowerCase();
			} else {
				filename = filename.substring(0, 1).toUpperCase();
			}
		}
		if (filepath == null || filepath.trim().equals("")) {
			fullfilename = filename;
		} else {
			fullfilename = filepath + "/" + filename;
		}
		FileOutputStream os = new FileOutputStream(fullfilename + ".java");
		return writeFile(os, filename, parttablename.toUpperCase(),
				packagename, ishaveconstructor, authorname);
	}

	private int writeFile(FileOutputStream os, String filename,
			String tablename, String packagename, int ishaveconstructor,
			String authorname) throws Exception, IOException {
		if (packagename != null && !packagename.trim().equals("")) {
			os.write(("package " + packagename + ";\n").getBytes());
		}
		if (authorname != null && !authorname.trim().equals("")) {
			os.write(("/**\n *\n *author:" + authorname + "\n*/\n").getBytes());
		}
		os.write(("public class " + filename + "{\n").getBytes());
		// String sql="select * from "+tablename+" where 1>2";
		String sql = "SELECT * from cps.v_dict_fielddesc where table_name='"
				+ tablename + "'";
		List list = openSelectbyList(sql);
		if (list != null) {
			String curcolinfo = null;
			for (int i = 0; i < list.size(); i++) {
				Hashtable ht = (Hashtable) list.get(i);
				String columnName = ht.get("column_name").toString()
						.toLowerCase();
				String columnType = getTypeName(ht.get("data_type").toString());
				String columnment = ht.get("comments").toString();
				curcolinfo = "  private " + columnType + " " + columnName
						+ ";//" + columnment + "     " + (i + 1) + "\n";
				os.write(curcolinfo.getBytes());
			}
			os.write("\n".getBytes());
			if (ishaveconstructor == 1) {
				os.write(("  public " + filename + "(){\n\n  }\n").getBytes());
			}
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Hashtable ht = (Hashtable) list.get(i);
					String columnName = ht.get("column_name").toString()
							.toLowerCase();
					String columnType = getTypeName(ht.get("data_type")
							.toString());
					String columnment = ht.get("comments").toString();
					String curcolumnName = null;
					if (columnName.length() > 1) {
						curcolumnName = columnName.substring(0, 1)
								.toUpperCase()
								+ columnName.substring(1).toLowerCase();
					} else {
						curcolumnName = columnName.toUpperCase();
					}
					curcolinfo = "  public " + columnType + " get"
							+ curcolumnName + "(){\n    return this."
							+ columnName + ";//����" + columnment + "\n  }\n";
					os.write(curcolinfo.getBytes());
					curcolinfo = "  public void set" + curcolumnName + "("
							+ columnType + " " + columnName + "){\n    this."
							+ columnName + "=" + columnName + ";//����"
							+ columnment + "\n  }\n";
					os.write(curcolinfo.getBytes());
				}
			}
		}
		os.write("}".getBytes());
		os.close();
		return 1;
	}

	private String getTypeName(String dbtypename) {
		String typename = "";
		if (dbtypename.equalsIgnoreCase("NUMBER")) {
			typename = "int";
		} else if (dbtypename.equalsIgnoreCase("VARCHAR2")) {
			typename = "String";
		} else if (dbtypename.equalsIgnoreCase("DATE")) {
			typename = "java.util.Date";
		} else if (dbtypename.equalsIgnoreCase("TIMESTAMP(6)")) {
			typename = "java.sql.Timestamp";
		}
		return typename;
	}

	public boolean changepassword(String username, String oldpassword,
			String newpassword) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean runSqlByJdbc(String dmlSql, Object[] parameters)
			throws Exception {
		if (getLongtran() && islongtransuccess == false)
			return false;
		if (dmlSql == null)
			throw new Exception("��ִ��runSqlʱִ����䲻��ΪNull!");
		if (dmlSql.trim().equals(""))
			throw new Exception("��ִ��runSqlʱִ����䲻��Ϊ��!");
		PreparedStatement prepareStatement = null;
		oracle.sql.BLOB ioblob = null;
		oracle.sql.BLOB blob = null;
		Connection conn = getConnectionByJdbc();

		try {
			prepareStatement = conn.prepareStatement(dmlSql);
			if (prepareStatement == null)
				throw new Exception(
						"��ִ��runSqlʱconn.getConnection().prepareStatement()����NULL");
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == null) {
						// logger.info(i+" is null");
						prepareStatement.setString(i + 1, "");
					} else {
						Class parameterTypeClass = parameters[i].getClass();
						String parameterTypeName = parameterTypeClass.getName();
						// logger.info(i+"-->"+parameterTypeName+"-->"+parameters[i].toString());
						if (parameterTypeName.equals("java.lang.Integer")
								|| parameterTypeName.equals("int")) {
							prepareStatement.setInt(i + 1,
									((Integer) parameters[i]).intValue());
						} else {
							if (parameterTypeName
									.equalsIgnoreCase("java.lang.Float")
									|| parameterTypeName
											.equalsIgnoreCase("float")) {
								prepareStatement.setFloat(i + 1,
										((Float) parameters[i]).floatValue());
							} else {
								if (parameterTypeName
										.equals("java.lang.String")) {

									String tmpvalue = "";
									if (parameters[i] != null) {
										tmpvalue = com.jl.util.StringUtils
												.replaceString(parameters[i]
														.toString(), "'", "\"");
									}
									prepareStatement.setString(i + 1, tmpvalue);

								} else {
									if (parameterTypeName
											.equals("java.sql.Timestamp")) {
										prepareStatement
												.setTimestamp(
														i + 1,
														parameters[i] == null ? null
																: (java.sql.Timestamp) parameters[i]);
									} else {
										if (parameterTypeName
												.equals("java.sql.Date")) {
											prepareStatement
													.setDate(
															i + 1,
															parameters[i] == null ? null
																	: (java.sql.Date) parameters[i]);
										} else {
											if (parameterTypeName
													.equalsIgnoreCase("java.lang.Long")
													|| parameterTypeName
															.equalsIgnoreCase("long")) {
												prepareStatement.setLong(i + 1,
														((Long) parameters[i])
																.longValue());
											} else {
												if (parameterTypeName
														.equals("java.io.File")) {
													// ������Ӧ���ļ���
													ioblob = convertFiletoBlob((File) parameters[i]);
													prepareStatement.setObject(
															i, ioblob,
															Types.BLOB);
												} else {
													if (parameterTypeName
															.equals("java.io.ByteArrayInputStream")) {
														blob = convertStringtoBlob((ByteArrayInputStream) parameters[i]);
														prepareStatement
																.setObject(
																		i,
																		blob,
																		Types.BLOB);
													}
												}

											}
										}
									}
								}
							}
						}
					}
				}
			}
			prepareStatement.executeUpdate();
			conn.commit();
			return true;
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			if (prepareStatement != null) {
				prepareStatement.close();
				prepareStatement = null;
			}
			if (blob != null)
				oracle.sql.BLOB.freeTemporary(blob);
			if (ioblob != null)
				oracle.sql.BLOB.freeTemporary(ioblob);
			conn.close();
		}
	}
}
