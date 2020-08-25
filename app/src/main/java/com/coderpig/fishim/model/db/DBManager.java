package com.coderpig.fishim.model.db;

import android.content.Context;

import com.coderpig.fishim.model.dao.InviteTableDao;
import com.coderpig.fishim.model.dao.ContactTabDao;

/**
 *联系人和邀请信息表的操作类的对象 的管理类
 */

public class DBManager {

    private final DBHelper dbHelper;
    private final ContactTabDao contactTabDao;
    private final InviteTableDao inviteTableDao;

    public DBManager(Context context, String name) {
        //创建数据库
        dbHelper = new DBHelper(context,name);

        //创建该数据库中两张表的操作类
        contactTabDao = new ContactTabDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
    }

    public ContactTabDao getContactTabDao() {
        return contactTabDao;
    }

    public InviteTableDao getInviteTableDao() {
        return inviteTableDao;
    }

    /**
     * 关闭数据库的方法
     */
    public void close() {
        dbHelper.close();
    }
}
