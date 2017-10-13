package com.jl.util;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import com.jl.util.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import oracle.sql.CLOB;

public final class RefUtil {
	/**
	 * 将包含Hashtable或TreeMap的集合中的key转换为List
	 * 
	 * @param list
	 * @return
	 */
	public static List getColNamesFromList(List list) {
		if (list == null) {
			return null;
		}
		List collist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if (obj instanceof Hashtable) {
				Hashtable ht = (Hashtable) obj;
				Enumeration e = ht.keys();
				while (e.hasMoreElements()) {
					String ele = e.nextElement().toString();
					collist.add(ele);
				}
				break;
			} else if (obj instanceof TreeMap) {
				TreeMap ht = (TreeMap) obj;
				Set s = ht.keySet();
				Iterator it = s.iterator();
				while (it.hasNext()) {
					String ele = it.next().toString();
					collist.add(ele);
				}
				break;
			}
		}
		return collist;
	}

	/**
	 * 通过数据Bean获取字段参数
	 * 
	 * @param bean
	 *            Object
	 * @return String[]
	 */
	private static String[] getParamNames(Class beanclass) {
		String[] colarr = null;
		Field[] fields = beanclass.getDeclaredFields();
		Field[] parentfields = beanclass.getSuperclass().getDeclaredFields();
		int cnt = fields.length;
		// System.out.println("parentclassname--->"+beanclass.getSuperclass().getName());
		if (!beanclass.getSuperclass().getName().equalsIgnoreCase(
				"java.lang.Object")) {
			cnt = cnt + parentfields.length;
		}
		colarr = new String[cnt];
		// //System.out.println("fields length-->"+fields.length);
		for (int i = 0; i < fields.length; i++) {
			String tmp = fields[i].getName();
			// //System.out.println("fields["+i+"]"+tmp);
			if (tmp.length() > 1) {
				tmp = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
			} else if (tmp.length() == 1) {
				tmp = tmp.toUpperCase();
			}
			colarr[i] = tmp;
		}
		if (!beanclass.getSuperclass().getName().equalsIgnoreCase(
				"java.lang.Object")) {
			for (int i = fields.length; i < cnt; i++) {
				String tmp = parentfields[i - fields.length].getName();
				// //System.out.println("fields["+i+"]"+tmp);
				if (tmp.length() > 1) {
					tmp = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
				} else if (tmp.length() == 1) {
					tmp = tmp.toUpperCase();
				}
				colarr[i] = tmp;
			}
		}
		return colarr;
	}

	/**
	 * 
	 * @param tablename
	 *            String
	 * @param columnname
	 *            String[]
	 * @param conditions
	 *            String
	 * @param type
	 *            SqlType
	 * @return String
	 */
	public static String getSQL(String tablename, String[] columnname,
			String conditions, String type) {
		String sql = "";
		String fields = "";
		String xyz = "";
		String updatefield = "";
		if (tablename.indexOf(".") == -1) {
			tablename = "cps." + tablename;
		}
		for (int i = 0; columnname != null && i < columnname.length; i++) {
			fields += (i < columnname.length - 1 ? columnname[i] + ","
					: columnname[i]);
			xyz += (i < columnname.length - 1 ? "?," : "?");
			updatefield += (i < columnname.length - 1 ? columnname[i] + "=?,"
					: columnname[i] + "=?");
		}
		fields = trimChar(fields, ",");
		xyz = trimChar(xyz, ",");
		updatefield = trimChar(updatefield, ",");
		if (type.equalsIgnoreCase(SqlType.Insert)) {
			sql = "insert into " + tablename + "(" + fields + ") values(" + xyz
					+ ")";
		} else if (type.equalsIgnoreCase(SqlType.Delete)) {
			sql = "delete from " + tablename;
		} else if (type.equalsIgnoreCase(SqlType.Update)) {
			sql = "update " + tablename + " set " + updatefield;
		} else if (type.equalsIgnoreCase(SqlType.Select)) {
			if (columnname == null) {
				sql = "select * from " + tablename;
			} else {
				sql = "select " + fields + " from " + tablename;
			}
		}
		if (conditions != null) {
			sql += " " + conditions;
		}
		return sql;
	}

	/**
	 * 去掉字符串最后一个字符
	 * 
	 * @param srcStr
	 *            String
	 * @param c
	 *            String
	 * @return String
	 */
	public static String trimChar(String srcStr, String c) {
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
	 * 如果Bean属性值为空，则返回false,否则为true
	 * 
	 * @param bean
	 *            Object
	 * @param fieldname
	 *            String
	 * @return boolean
	 */
	public static boolean isNullNotSetValue(Object bean, String fieldname) {
		boolean flag = false;
		Class beanClass = bean.getClass();
		Class parentClass = beanClass.getSuperclass();
		Method m = null;
		if (fieldname != null && !fieldname.equalsIgnoreCase("")) {
			try {
				m = beanClass.getDeclaredMethod("get" + fieldname, null);
				if (m != null) {
					Object rtn = m.invoke(bean, null);
					if (rtn != null
							&& !rtn.toString().trim().equalsIgnoreCase("")) {
						return true;
					}
				}
				if (!parentClass.getName().equalsIgnoreCase("java.lang.Object")) {
					m = parentClass.getDeclaredMethod("get" + fieldname, null);
					if (m != null) {
						Object rtn = m.invoke(bean, null);
						if (rtn != null
								&& !rtn.toString().trim().equalsIgnoreCase("")) {
							return true;
						}
					}
				}
			} catch (SecurityException ex) {
				ex.printStackTrace();
			} catch (NoSuchMethodException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static boolean isNullNotSetValueForException(Object bean,
			String fieldname) throws SecurityException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		boolean flag = false;
		Class beanClass = bean.getClass();
		Class parentClass = beanClass.getSuperclass();
		Method m = null;
		if (fieldname != null && !fieldname.equalsIgnoreCase("")) {
			try {
				m = beanClass.getDeclaredMethod("get" + fieldname, null);
				if (m != null) {
					Object rtn = m.invoke(bean, null);
					if (rtn != null
							&& !rtn.toString().trim().equalsIgnoreCase("")) {
						return true;
					}
				}
				m = parentClass.getDeclaredMethod("get" + fieldname, null);
				if (m != null) {
					Object rtn = m.invoke(bean, null);
					if (rtn != null
							&& !rtn.toString().trim().equalsIgnoreCase("")) {
						return true;
					}
				}
			} catch (SecurityException ex) {
				throw ex;
			} catch (NoSuchMethodException ex) {
				throw ex;
			} catch (IllegalAccessException e) {
				throw e;
			} catch (InvocationTargetException e) {
				throw e;
			}
		}
		return flag;
	}

	public static String getStringFromClob(CLOB clob) {
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

	// ------------------

	/**
	 * 从表单提交数据初始化Bean
	 */
	public static Object initBean(Object beanInstance, PageContext pageContext)
			throws Exception {
		ServletRequest request = pageContext.getRequest();
		Object beaninstance = beanInstance;
		try {
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanInstance.getClass());
			// 获取其属性描述
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			// 设置值的方法
			Method mSet = null;
			String FieldValue = "";
			for (int k = 0; k < pd.length; k++) {
				mSet = pd[k].getWriteMethod();
				if (mSet == null)
					continue;
				Object[] values = new Object[1];
				Class propertytype = pd[k].getPropertyType();
				FieldValue = request.getParameter(pd[k].getName());
				if (FieldValue == null)
					continue;
				if (propertytype.getName().equalsIgnoreCase("long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("int")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName()
						.equalsIgnoreCase("java.lang.Integer")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.String")) {
					values[0] = FieldValue;
					mSet.invoke(beaninstance, values);
				}
				if (propertytype.getName().equalsIgnoreCase(
						"java.sql.Timestamp")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getTimestampFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.sql.Date")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getDateFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return beaninstance;
	}

	// ------------------

	/**
	 * 从表单提交数据初始化Bean
	 */
	public static Object initBean(Class beanClass, PageContext pageContext)
			throws Exception {
		ServletRequest request = pageContext.getRequest();
		Object beaninstance = null;
		try {
			beaninstance = beanClass.newInstance();
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanClass);
			// 获取其属性描述
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			// 设置值的方法
			Method mSet = null;
			String FieldValue = "";
			for (int k = 0; k < pd.length; k++) {
				mSet = pd[k].getWriteMethod();
				if (mSet == null)
					continue;
				Object[] values = new Object[1];
				Class propertytype = pd[k].getPropertyType();
				FieldValue = request.getParameter(pd[k].getName());
				if (FieldValue == null)
					continue;
				if (propertytype.getName().equalsIgnoreCase("long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("int")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName()
						.equalsIgnoreCase("java.lang.Integer")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.String")) {
					values[0] = FieldValue;
					mSet.invoke(beaninstance, values);
				}
				if (propertytype.getName().equalsIgnoreCase(
						"java.sql.Timestamp")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getTimestampFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.sql.Date")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getDateFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return beaninstance;
	}

	public static Object initBeanByForm(Class beanClass, PageContext pageContext)
			throws Exception {
		ServletRequest request = pageContext.getRequest();
		Object beaninstance = null;
		try {
			beaninstance = beanClass.newInstance();
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanClass);
			// 获取其属性描述
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			// 设置值的方法
			Method mSet = null;
			String FieldValue = "";
			for (int k = 0; k < pd.length; k++) {
				mSet = pd[k].getWriteMethod();
				if (mSet == null)
					continue;
				Object[] values = new Object[1];
				Class propertytype = pd[k].getPropertyType();
				FieldValue = request.getParameter(pd[k].getName());
				if (FieldValue == null)
					continue;
				if (propertytype.getName().equalsIgnoreCase("long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("int")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName()
						.equalsIgnoreCase("java.lang.Integer")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.String")) {
					values[0] = FieldValue;
					mSet.invoke(beaninstance, values);
				}
				if (propertytype.getName().equalsIgnoreCase(
						"java.sql.Timestamp")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getTimestampFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.sql.Date")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getDateFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					} else {
						values[0] = null;
						mSet.invoke(beaninstance, values);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return beaninstance;
	}

	public static String getRandomColor() {
		String mystr = "#"; // /返回的颜色代码
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

	private static String getTypeName(String dbtypename) {
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

	public static void mkdir(String path) throws Exception {
		String msg = "";
		java.io.File dir;
		// 新建文件对象
		dir = new java.io.File(path);
		if (dir == null) {
			msg = "错误原因:对不起，不能创建空目录！";
			throw new Exception(msg);
		}
		if (dir.isFile()) {
			msg = "错误原因:已有同名文件" + dir.getAbsolutePath() + "存在。";
			throw new Exception(msg);
		}
		if (!dir.exists()) {
			boolean result = dir.mkdirs();
			if (result == false) {
				msg = "错误原因:目录" + dir.getAbsolutePath() + "创建失败，原因不明！";
				throw new Exception(msg);
			}
			// 如果成功创建目录，则无输出。
			// msg ="成功创建目录: <B>" + dir.getAbsolutePath() + "</B>";
		}
	}

	// emptybean是一个有对象数据库描述的对象(class_tablename,class_primary,class_schema)
	public static List convertListToBeans(List ls, Object emptybean)
			throws Exception {
		Class beanclass = emptybean.getClass();
		List beanls = new ArrayList();
		for (int i = 0; i < ls.size(); i++) {
			Hashtable ht = (Hashtable) ls.get(i);
			Object beaninstance;
			beaninstance = beanclass.newInstance();

			// 获取父类class的信息
			java.beans.BeanInfo parentinfo;
			parentinfo = java.beans.Introspector.getBeanInfo(beaninstance
					.getClass().getSuperclass());

			// 获取其属性描述
			java.beans.PropertyDescriptor parentpd[] = parentinfo
					.getPropertyDescriptors();
			// 设置值的方法
			Method mParentSet = null;
			// 设置读的方法
			Method mParentGet = null;
			for (int k = 0; k < parentpd.length; k++) {
				if (parentpd[k].getName().equalsIgnoreCase("class_tablename")
						|| parentpd[k].getName().equalsIgnoreCase(
								"class_primary")
						|| parentpd[k].getName().equalsIgnoreCase(
								"class_schema")) {
					mParentSet = parentpd[k].getWriteMethod();
					mParentGet = parentpd[k].getReadMethod();
					Object value;
					if (mParentGet == null)
						continue;
					value = mParentGet.invoke(emptybean, null);

					// 利用Java的反射极致调用对象的某个set方法，并将值设置进去
					Object[] values = new Object[1];
					values[0] = value;
					if (mParentSet == null)
						continue;
					mParentSet.invoke(beaninstance, values);
				}
			}
			beaninstance = InitBean(beaninstance, ht);
			beanls.add(beaninstance);
		}
		return null;
	}

	public static Object InitBean(Class beanclsss, Hashtable ht)
			throws Exception {
		Object beaninstance = null;
		try {
			beaninstance = beanclsss.newInstance();
			if (ht != null) {
				for (Iterator itr = ht.keySet().iterator(); itr.hasNext();) {
					String key = (String) itr.next();
					Object value = (Object) ht.get(key);
					// 获取对应class的信息
					java.beans.BeanInfo info;
					info = java.beans.Introspector.getBeanInfo(beanclsss);

					// 获取其属性描述
					java.beans.PropertyDescriptor pd[] = info
							.getPropertyDescriptors();
					// 设置值的方法
					Method mSet = null;
					for (int k = 0; k < pd.length; k++) {
						if (pd[k].getName().equalsIgnoreCase(key)) {
							mSet = pd[k].getWriteMethod();
							if (mSet == null)
								continue;
							// 利用Java的反射极致调用对象的某个set方法，并将值设置进去
							Object[] values = new Object[1];
							Class propertytype = pd[k].getPropertyType();
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Object")) {
								// blob字段
								if (value.equals("") == false) {
									values[0] = value;
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase("long")) {
								if (value.equals("") == false) {
									values[0] = Long.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Long")) {
								if (value.equals("") == false) {
									values[0] = Long.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase("int")) {
								if (value.equals("") == false) {
									values[0] = Integer.valueOf(value
											.toString());
									mSet.invoke(beaninstance, values);

								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Integer")) {
								if (value.equals("") == false) {
									values[0] = Integer.valueOf(value
											.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"double")) {
								if (value.equals("") == false) {
									values[0] = Double
											.valueOf(value.toString());
									mSet.invoke(beaninstance, values);

								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Double")) {
								if (value.equals("") == false) {
									values[0] = Double
											.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName()
									.equalsIgnoreCase("float")) {
								if (value.equals("") == false) {
									values[0] = Float.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Float")) {
								if (value.equals("") == false) {
									values[0] = Float.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.String")) {
								values[0] = value;
								mSet.invoke(beaninstance, values);
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Timestamp")) {
								if (value.equals("") == false) {
									if (value instanceof java.sql.Timestamp) {
										values[0] = value;
										mSet.invoke(beaninstance, values);
									} else {
										values[0] = DateUtil
												.getTimestampFromString(value
														.toString());
										mSet.invoke(beaninstance, values);
									}
									/*
									 * if (value instanceof String) { values[0] =
									 * DateUtil.getTimestampFromString(value.toString());
									 * mSet.invoke(beaninstance, values); } else {
									 * values[0] = value;
									 * mSet.invoke(beaninstance, values); }
									 */
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Date")) {
								if (value.equals("") == false) {
									if (value instanceof java.sql.Date) {
										values[0] = value;
										mSet.invoke(beaninstance, values);
									} else {
										values[0] = DateUtil
												.getDateFromString(value
														.toString());
										mSet.invoke(beaninstance, values);
									}
									/*
									 * if (value instanceof String) { values[0] =
									 * DateUtil.getDateFromString(value.toString());
									 * mSet.invoke(beaninstance, values); } else {
									 * values[0] = value;
									 * mSet.invoke(beaninstance, values); }
									 */
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Blob")) {
								if (value.equals("") == false) {
									values[0] = value;
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Clob")) {
								if (value.equals("") == false) {
									values[0] = value;
									mSet.invoke(beaninstance, values);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return beaninstance;
	}

	public static Object InitBean(Object beaninstance, Hashtable ht)
			throws Exception {
		try {
			if (ht != null) {
				for (Iterator itr = ht.keySet().iterator(); itr.hasNext();) {
					String key = (String) itr.next();
					Object value = (Object) ht.get(key);
					// 获取对应class的信息
					java.beans.BeanInfo info;
					info = java.beans.Introspector.getBeanInfo(beaninstance
							.getClass());

					// 获取其属性描述
					java.beans.PropertyDescriptor pd[] = info
							.getPropertyDescriptors();
					// 设置值的方法
					Method mSet = null;
					for (int k = 0; k < pd.length; k++) {
						if (pd[k].getName().equalsIgnoreCase(key)) {
							mSet = pd[k].getWriteMethod();
							if (mSet == null)
								continue;
							// 利用Java的反射极致调用对象的某个set方法，并将值设置进去
							Object[] values = new Object[1];
							Class propertytype = pd[k].getPropertyType();
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Object")) {
								// blob字段
								if (value.equals("") == false) {
									values[0] = value;
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase("long")) {
								if (value.equals("") == false) {
									values[0] = Long.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Long")) {
								if (value.equals("") == false) {
									values[0] = Long.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase("int")) {
								if (value.equals("") == false) {
									values[0] = Integer.valueOf(value
											.toString());
									mSet.invoke(beaninstance, values);

								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Integer")) {
								if (value.equals("") == false) {
									values[0] = Integer.valueOf(value
											.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"double")) {
								if (value.equals("") == false) {
									values[0] = Double
											.valueOf(value.toString());
									mSet.invoke(beaninstance, values);

								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Double")) {
								if (value.equals("") == false) {
									values[0] = Double
											.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName()
									.equalsIgnoreCase("float")) {
								if (value.equals("") == false) {
									values[0] = Float.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.Float")) {
								if (value.equals("") == false) {
									values[0] = Float.valueOf(value.toString());
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.lang.String")) {
								values[0] = value;
								mSet.invoke(beaninstance, values);
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Timestamp")) {
								if (value.equals("") == false) {
									if (value instanceof java.sql.Timestamp) {
										values[0] = value;
										mSet.invoke(beaninstance, values);
									} else {
										values[0] = DateUtil
												.getTimestampFromString(value
														.toString());
										mSet.invoke(beaninstance, values);
									}
									/*
									 * if (value instanceof String) { values[0] =
									 * DateUtil.getTimestampFromString(value.toString());
									 * mSet.invoke(beaninstance, values); } else {
									 * values[0] = value;
									 * mSet.invoke(beaninstance, values); }
									 */
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Date")) {
								if (value.equals("") == false) {
									if (value instanceof java.sql.Date) {
										values[0] = value;
										mSet.invoke(beaninstance, values);
									} else {
										values[0] = DateUtil
												.getDateFromString(value
														.toString());
										mSet.invoke(beaninstance, values);
									}
									/*
									 * if (value instanceof String) { values[0] =
									 * DateUtil.getDateFromString(value.toString());
									 * mSet.invoke(beaninstance, values); } else {
									 * values[0] = value;
									 * mSet.invoke(beaninstance, values); }
									 */
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Blob")) {
								if (value.equals("") == false) {
									values[0] = value;
									mSet.invoke(beaninstance, values);
								}
							}
							if (propertytype.getName().equalsIgnoreCase(
									"java.sql.Clob")) {
								if (value.equals("") == false) {
									values[0] = value;
									mSet.invoke(beaninstance, values);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return beaninstance;
	}

	public static Hashtable Bean2Hashtable(Object beaninstance)
			throws Exception {
		Hashtable ht = new Hashtable();
		try {
			// 获取对应class的信息
			java.beans.BeanInfo info;
			info = java.beans.Introspector.getBeanInfo(beaninstance.getClass());
			// 获取其属性描述
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			// 设置值的方法
			Method mGet = null;
			for (int k = 0; k < pd.length; k++) {
				mGet = pd[k].getReadMethod();
				if (mGet == null)
					continue;
				Object obj = mGet.invoke(beaninstance, null);
				if(obj==null)obj="";
				ht.put(pd[k].getName(), obj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return ht;
	}

	public static String getMethodName(Class beanClass, String compareMethodName) {
		Method m[] = beanClass.getDeclaredMethods();

		String returnMethodName = "";
		for (int i = 0; i < m.length; i++) {
			String methodName = m[i].getName();
			if (methodName.compareToIgnoreCase(compareMethodName) == 0) {
				returnMethodName = methodName;
				break;
			}
		}
		if (beanClass.getSuperclass() != null) {
			Method parentm[] = beanClass.getSuperclass().getDeclaredMethods();
			for (int i = 0; i < parentm.length; i++) {
				String methodName = parentm[i].getName();
				if (methodName.compareToIgnoreCase(compareMethodName) == 0) {
					returnMethodName = methodName;
					break;
				}
			}
		}
		return returnMethodName;
	}

	/**
	 * 获取Bean中的字段的值
	 * 
	 * @param beanClass
	 *            Class
	 * @param getFieldValue
	 *            String
	 * @return String
	 */
	public static Object getFieldValue(Object bean, String FieldName)
			throws Exception {
		Object rtn = null;
		// 获取对应class的信息
		java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(bean
				.getClass());
		// 获取其属性描述
		java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
		// 读值的方法
		Method mGet = null;
		for (int k = 0; k < pd.length; k++) {
			if (pd[k].getName().equalsIgnoreCase(FieldName)) {
				mGet = pd[k].getReadMethod();
				if (mGet == null)
					continue;
				Object[] O = new Object[0];
				rtn = mGet.invoke(bean, O);
			}
		}
		return rtn;
	}

	public static String getHiddenControlFromBean(Object bean) throws Exception {
		StringBuffer hiddenbuffer = new StringBuffer();
		Object rtn = null;
		// 获取对应class的信息
		java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(bean
				.getClass());
		// 获取其属性描述
		java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
		// 读值的方法
		Method mGet = null;
		for (int k = 0; k < pd.length; k++) {
			String propertytype = pd[k].getPropertyType().getName();
			mGet = pd[k].getReadMethod();
			if (mGet == null)
				continue;
			String fieldname = pd[k].getName();
			Object[] O = new Object[0];
			Object value = mGet.invoke(bean, O);
			if (propertytype.equalsIgnoreCase("long")) {
				hiddenbuffer.append("<input type='hidden' name='" + fieldname
						+ "' value='" + value + "'>");
			}
			if (propertytype.equalsIgnoreCase("java.lang.Long")) {
				if (value != null) {
					Long L = (Long) value;
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + L.toString() + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}
			if (propertytype.equalsIgnoreCase("int")) {
				hiddenbuffer.append("<input type='hidden' name='" + fieldname
						+ "' value='" + value + "'>");
			}
			if (propertytype.equalsIgnoreCase("java.lang.Integer")) {
				if (value != null) {
					Integer I = (Integer) value;
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + I.toString() + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}
			if (propertytype.equalsIgnoreCase("double")) {
				hiddenbuffer.append("<input type='hidden' name='" + fieldname
						+ "' value='" + value + "'>");
			}
			if (propertytype.equalsIgnoreCase("java.lang.Double")) {
				if (value != null) {
					Double D = (Double) value;
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + D.toString() + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}
			if (propertytype.equalsIgnoreCase("float")) {
				hiddenbuffer.append("<input type='hidden' name='" + fieldname
						+ "' value='" + value + "'>");
			}
			if (propertytype.equalsIgnoreCase("java.lang.Float")) {
				if (value != null) {
					Float F = (Float) value;
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + F.toString() + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}
			if (propertytype.equalsIgnoreCase("java.lang.String")) {
				if (value != null) {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + value + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}
			if (propertytype.equalsIgnoreCase("java.sql.Timestamp")) {
				if (value != null) {
					String datetime = DateUtil
							.getStringFromDateTime((java.sql.Timestamp) value);
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + datetime + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}

			if (propertytype.equalsIgnoreCase("java.sql.Date")) {
				if (value != null) {
					String datetime = DateUtil
							.getStringFromDate((java.sql.Date) value);
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value='" + datetime + "'>");
				} else {
					hiddenbuffer.append("<input type='hidden' name='"
							+ fieldname + "' value=''>");
				}
			}
			if (k < pd.length - 1) {
				hiddenbuffer.append("\n");
			}
		}
		return hiddenbuffer.toString();
	}

	/**
	 * 设置Bean中的字段的值
	 * 
	 * @param beanClass
	 *            Class
	 * @param getFieldValue
	 *            String
	 * @return String
	 */
	public static boolean setFieldValue(Object beaninstance, String FieldName,
			String FieldValue) throws Exception {
		Object rtn = null;
		// 获取对应class的信息

		java.beans.BeanInfo info = java.beans.Introspector
				.getBeanInfo(beaninstance.getClass());
		// 获取其属性描述
		java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
		// 设置值的方法
		Method mSet = null;
		for (int k = 0; k < pd.length; k++) {
			if (pd[k].getName().equalsIgnoreCase(FieldName)) {
				mSet = pd[k].getWriteMethod();
				if (mSet == null)
					continue;
				Object[] values = new Object[1];
				Class propertytype = pd[k].getPropertyType();
				if (propertytype.getName().equalsIgnoreCase("long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Long")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Long.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("int")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName()
						.equalsIgnoreCase("java.lang.Integer")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Integer.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Double")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Double.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.Float")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = Float.valueOf(FieldValue.toString());
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.lang.String")) {
					values[0] = FieldValue;
					mSet.invoke(beaninstance, values);
				}
				if (propertytype.getName().equalsIgnoreCase(
						"java.sql.Timestamp")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getTimestampFromString(FieldValue);
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase(
						"java.math.BigDecimal")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = FieldValue;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.sql.Date")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = DateUtil.getDateFromString(FieldValue);
						;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.sql.Blob")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = FieldValue;
						mSet.invoke(beaninstance, values);
					}
				}
				if (propertytype.getName().equalsIgnoreCase("java.sql.Clob")) {
					if (FieldValue.equalsIgnoreCase("") == false) {
						values[0] = FieldValue;
						mSet.invoke(beaninstance, values);
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取Bean中的字段的值
	 * 
	 * @param beanClass
	 *            Class
	 * @param getFieldValue
	 *            String
	 * @return String
	 */
	public static String getFieldType(Object bean, String FieldName)
			throws Exception {
		String rtn = "";
		// 获取对应class的信息

		java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(bean
				.getClass());
		// 获取其属性描述
		java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
		for (int k = 0; k < pd.length; k++) {
			if (pd[k].getName().equalsIgnoreCase(FieldName)) {
				rtn = pd[k].getPropertyType().getName();
			}
		}
		return rtn;
	}

	/**
	 * 获取Bean中的字段
	 * 
	 * @param beanClass
	 *            Class
	 * @param compareFieldName
	 *            String
	 * @return String
	 * @throws BaseAppException
	 */
	public static String getFieldName(Class beanClass, String compareFieldName)
			throws Exception {
		String returnfieldname = "";
		// 获取对应class的信息
		java.beans.BeanInfo info;
		info = java.beans.Introspector.getBeanInfo(beanClass);

		// 获取其属性描述
		java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
		for (int k = 0; k < pd.length; k++) {
			if (pd[k].getName().equalsIgnoreCase(compareFieldName)) {
				returnfieldname = pd[k].getName();
			}
		}
		return returnfieldname;
	}

	/**
	 * 通过数据Bean获取字段参数
	 * 
	 * @param bean
	 *            Object
	 * @return String[]
	 * @throws Exception
	 */
	public static String[] getParamNames(Object bean) throws Exception {
		String[] colarr = null;
		try {
			Class beanclass = bean.getClass();
			java.beans.BeanInfo info = java.beans.Introspector
					.getBeanInfo(beanclass);
			// 获取其属性描述
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			colarr = new String[pd.length];
			for (int k = 0; k < pd.length; k++) {
				colarr[k] = pd[k].getName();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return colarr;
	}

	/**
	 * 
	 * @param tablename
	 *            String
	 * @param columnname
	 *            String[]
	 * @param conditions
	 *            String
	 * @param type
	 *            SqlType
	 * @return String
	 */
	public static String getSQL(String tablename, String[] columnname,
			String conditions, String type, Object bean) {
		String sql = "";
		String fields = "";
		String xyz = "";
		String updatefield = "";
		if (tablename.indexOf(".") == -1) {
			tablename = "cps." + tablename;
		}
		for (int i = 0; columnname != null && i < columnname.length; i++) {
			Class beanClass = bean.getClass();
			String compareMethodName = "get" + columnname[i];
			String beanMethodName = RefUtil.getMethodName(beanClass,
					compareMethodName);
			if (!beanMethodName.equalsIgnoreCase("")) {
				Method m = null;
				try {
					m = beanClass.getMethod(beanMethodName, null);
				} catch (NoSuchMethodException e) {
					// //System.out.println("no found method:" +
					// e.getMessage());
					continue;
				}
				if (m != null) {
					try {
						Object rtn = m.invoke(bean, null);
						if (rtn != null) {
							fields += (i < columnname.length - 1 ? columnname[i]
									+ ","
									: columnname[i]);
							xyz += (i < columnname.length - 1 ? "?," : "?");
							updatefield += (i < columnname.length - 1 ? columnname[i]
									+ "=?,"
									: columnname[i] + "=?");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		fields = trimChar(fields, ",");
		xyz = trimChar(xyz, ",");
		updatefield = trimChar(updatefield, ",");
		if (type.equalsIgnoreCase(SqlType.Insert)) {
			sql = "insert into " + tablename + "(" + fields + ") values(" + xyz
					+ ")";
		} else if (type.equalsIgnoreCase(SqlType.Delete)) {
			sql = "delete from " + tablename;
		} else if (type.equalsIgnoreCase(SqlType.Update)) {
			sql = "update " + tablename + " set " + updatefield;
		} else if (type.equalsIgnoreCase(SqlType.Select)) {
			if (columnname == null) {
				sql = "select * from " + tablename;
			} else {
				sql = "select " + fields + " from " + tablename;
			}
		}
		sql += " " + conditions;
		return sql;
	}
}
