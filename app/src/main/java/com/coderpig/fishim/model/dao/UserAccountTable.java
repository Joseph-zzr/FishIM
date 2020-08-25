package com.coderpig.fishim.model.dao;

/**
 *储存用户信息
 */

public class UserAccountTable {
    public static final String TAB_NAME="tab_account";
    public static final String COL_NAME="name";
    public static final String COL_HXID="hxid";
    public static final String COL_NICK="nick";
    public static final String COL_PHOTO="photo";

    //建表语句
    public static final String CREATE_TAB="create table "
            + TAB_NAME + " ("
            + COL_HXID + " text primary key, "
            + COL_NAME + " text,"
            + COL_NICK + " text,"
            + COL_PHOTO + " text);";


}
