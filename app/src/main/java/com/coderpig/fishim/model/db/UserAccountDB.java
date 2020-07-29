package com.coderpig.fishim.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coderpig.fishim.model.dao.UserAccountTable;

import androidx.annotation.Nullable;

public class UserAccountDB extends SQLiteOpenHelper {
    public UserAccountDB(@Nullable Context context) {
        super(context, "account.db",null,1);
    }

    /**
     * 数据库创建时候调用
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表的语句
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    /**
     * 数据库更新的时候调用
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
