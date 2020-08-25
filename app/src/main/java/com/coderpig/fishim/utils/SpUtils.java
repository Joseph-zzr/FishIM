package com.coderpig.fishim.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.coderpig.fishim.IMApplication;

/**
 * 监听全局联系人变化
 *
 * 保存
 *
 * 获取
 */

public class SpUtils {
    //有新的邀请
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    private SpUtils(){

    }

    //单例
    public static SpUtils getInstance(){
        if (mSp == null){
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    /**
     * 保存
     * @param key
     * @param values
     */
    public void save(String key,Object values){
        if (values instanceof String){
            mSp.edit().putString(key, (String) values).commit();
        }else if (values instanceof Boolean){
            mSp.edit().putBoolean(key, (Boolean) values).commit();
        }else if (values instanceof Integer){
            mSp.edit().putInt(key, (Integer) values).commit();
        }
    }

    /**
     * 获取string类型的数据
     * @param key
     * @return
     */
    public String getString(String key,String defValue){
        return mSp.getString(key,defValue);
    }

    public Boolean getBoolen(String key,boolean defValue){
        return mSp.getBoolean(key,defValue);
    }

    public int getInteger(String key,int defValue){
        return mSp.getInt(key,defValue);
    }

}
