package com.jl.dao.base.impl;

import java.sql.Connection;

//数据库连接类 gaozp
public final class AppConnection {
    private Connection connection;
    private String id;
	public Connection getConnection() {
		return connection;
	}
	public String getId() {
		return id;
	}
	public AppConnection(Connection connection,
			String id)
	{
		this.connection=connection;
		this.id=id;
	}
}
