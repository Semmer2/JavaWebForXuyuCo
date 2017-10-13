package com.jl.util;


public abstract class SqlType {
    public final static String Insert = "Insert";
    public final static String Delete = "Delete";
    public final static String Update = "Update";
    public final static String Select = "Select";
    public final static int int_insert = 1;
    public final static int int_update = 2;
    public final static int int_delete = 3;
    public final static int operate_success = 1;
    public final static int operate_fail = 0;

    public static boolean isNullAfterWhere(String sql) {
        String[] temp = sql.split("where");
        return temp.length == 1 ? true : false;
    }
}
