package com.jl.dao.base.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.sql.BLOB;

public class BlobObject {
	private AppConnection conn;
	private PreparedStatement stmt;
	private ResultSet rset;
	private BLOB blob;
	public void close() throws Exception
	{
		if(rset!=null)
		{
			rset.close();
			rset=null;
		}
		if(stmt!=null)
		{
			stmt.close();
			stmt=null;
		}
		if(conn!=null)
		{
			conn.getConnection().setAutoCommit(true);
			ConnectionMap.remove(conn.getId());
			conn.getConnection().close();					
		}
	}
	public BlobObject(ResultSet rset,PreparedStatement stmt,AppConnection conn,BLOB blob)
	{
		this.rset=rset;
		this.stmt=stmt;
		this.conn=conn;
		this.blob=blob;
	}
	public oracle.sql.BLOB getBlob() {
		return blob;
	}
}
