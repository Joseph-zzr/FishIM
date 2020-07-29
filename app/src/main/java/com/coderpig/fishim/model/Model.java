package com.coderpig.fishim.model;

import android.content.Context;

import com.coderpig.fishim.model.dao.UserAccountDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作用：数据模型层全局类
 * 所有信息进出必须经过该类
 */

public class Model {
    private Context mContext;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    //创建对象
    private static Model model = new Model();
    private UserAccountDao userAccountDao;

    //私有化构造
    private Model(){

    }
    //获取单例对象
    public static Model getInstance(){
        return model;
    }
    //初始化方法
    public void init(Context context){
        mContext = context;

        //创建用户账号数据库的操作类对象
        userAccountDao = new UserAccountDao(context);
    }

    /**
     * 获取全局线程池对象
     * @return
     */
    public ExecutorService getGlobalThreadPool(){
        return executorService;
    }

    /**
     * 用户登录成功后的处理方法
     */
    public void logininSuccess() {
    }

    /**
     * 获取用户账号数据库的操作类对象
     * @return
     */
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }
}
