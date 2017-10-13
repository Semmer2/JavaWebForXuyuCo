package com.jl.dao.base.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.jl.dao.base.impl.BlobObject;


public interface DBASession {
	public int getTransuccess() throws Exception;
	public Object startlongTran() throws Exception;
	public void setlongTranFail(Object tranObject);
	public boolean endlongTran(Object tranObject) throws Exception;
	public Connection getConnection() throws Exception;
	public List getColNames(String sql) throws Exception;	
	public List openSelectbyTreeList(String s) throws Exception;
	public List openSelectbySortList(String s) throws Exception;
	public String[] getDBColNames(String s) throws Exception;
	public List openSelectbyList(String s) throws Exception;
	public List openSelectbyList(String s, Object[] parameters)	throws Exception;
	public Object openSelectByObject(String fieldName, String sql) throws Exception;
	public int getRecordNum(String strSql) throws Exception;
	public boolean runSqlByJdbc(String dmlSql, Object[] parameters) throws Exception; 
	public boolean runSql(String dmlSql, Object[] parameters) throws Exception; 
	public boolean runSql(String[] dmlSql, Object[][] parameters) throws Exception;
	public boolean runSql(String[] dmlSql) throws Exception;	
	public boolean runSql(String fullSql) throws Exception;	
	public boolean execProcedure(String procedurename, Object[] parameters)	throws Exception;
	public boolean execProcedure(String procedurename, Object[][] parameters) throws Exception;
	public boolean execProcedureWithInOut(String procedurename,	Object[][] parameters) throws Exception;
	public boolean execProcedure(String procedurename, Object[][] parameters,String objecttypename, String listtypename, Class oprclass)throws Exception;
	public boolean isOpen();
	public void close() throws Exception;
	public Object execFunction(String functionname, Object[][] parameters,int returntype) throws Exception;
	public List execProcedureByList(String procedurename, Object[] parameters) throws Exception;
	public int execBatchSql(List batchSql) throws Exception;
	public List execProcedureByList_readOnly(String procedurename,Object[] parameters) throws Exception;
	public boolean rollbacklongTran(Object tranObject) throws Exception;
	public List executeQueryProc(String procname, Object[] param) throws Exception;
	public List executeQueryProc(String procname, String[] param) throws Exception;
	public Object execProcedureInTrans(String functionname,Object[][] parameters, int returntype) throws Exception;	
	public List openSelectbyNum(String s) throws Exception;
	public Object executeScalar(String sql) throws Exception;
	public boolean common_operator(String tablename, String fieldname,
			String values, String date_fname, String date_value, String type,
			String mainid) throws Exception;
	public int getCountNumber(String tablename, String condition) throws Exception;
	public int getCountNumber(String tmp_sql) throws Exception;
	public boolean runSql(String sql, String[] param, Object bean) throws Exception;
	public boolean runSql(String[] sql, String[][] param, Object[] bean,Object[][] tialjianvalues) throws Exception;
	public boolean runSql(String[] sql, String[][] param, Object[] bean) throws Exception;	
	public boolean delete(Object bean) throws Exception;
	public boolean delete(String[] tjnames,String[] tjvalues, String tablename) throws Exception;
	public boolean delete(String[] tablename, String[] conditions) throws Exception;
	public boolean save(Object bean) throws Exception;
	public boolean insert(String tablename, Object bean) throws Exception;
	public boolean update(String tablename, Object bean, String conditions,String[] conditionvalues) throws Exception;
	public boolean update(String tablename, Object bean) throws Exception;
	public Object initBean(String tablename, String conditions,	Class beanclass, Object[] preparam) throws Exception;	
	//public Object initBean(String tablename, String conditions, Class beanclass) throws Exception; //���Ƽ�ʹ��
	public Object initBeanBySql(String tablename, String sql, Class beanclass) throws Exception;
	public Object initBeanBySql(String tablename, String sql, Class beanclass,Object[] preparam) throws Exception;	
	public Object initBeanBySql(String[] paramnames, String sql, Class beanclass) throws Exception;
	public Object initBeanBySql(String sql, Class beanclass) throws Exception;
	public Object initBeanBySql(String sql, Object[] parameters, Class beanclass) throws Exception;
	public Object initBean(String tablename, String conditions, Object bean) throws Exception;
	public Object initBean_clob(String tablename, String conditions,Class beanclass) throws Exception;
	public List getBeansBySql(String[] param, String tablename,String conditions, Class beanclass) throws Exception;
	public List getBeansBySql(String[] param, String sql, Class beanclass) throws Exception;
	public List getBeansBySql(String[] param, String sql,Class beanclass, Object[] preparam) throws  Exception;
	public List getBeansBySql(String tablename, String sql, Class beanclass) throws Exception;
	public List getBeansBySql(String tablename, String sql, Class beanclass,Object[] preparam) throws Exception;
	public List getBeans(String tablename, String conditions, Class beanclass,Object[] preparam)throws Exception;
	public List getBeans(String tablename, String conditions, Class beanclass) throws Exception;
	public boolean saveBeans(String[] tablename, Object[] bean, String[] sqltype,String[] conditions) throws Exception;
	public int genBeanClassFile(String tablename, String filepath,String filename, String packagename, int ishaveconstructor,String authorname) throws Exception;
	public BlobObject getBlobObject(String s, Object[] parameters,String blobfieldname) throws Exception;
	public boolean changepassword(String username,String oldpassword,String newpassword);
	public HttpServletRequest getPagerequest();
	
}