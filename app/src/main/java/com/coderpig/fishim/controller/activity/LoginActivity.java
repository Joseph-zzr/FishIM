package com.coderpig.fishim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coderpig.fishim.R;
import com.coderpig.fishim.model.Model;
import com.coderpig.fishim.model.bean.UserInfo;
import com.coderpig.fishim.utils.LogUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends AppCompatActivity {

    private TextView tv_Register;
    private EditText et_Register;
    private TextView tv_Code;
    private EditText et_Code;
    private Button btn_Register;
    private Button btn_SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        initView();

        //初始化监听
        initListener();
    }

    private void initListener() {
        //注册按钮
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });

        //登录按钮
        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     *登录的业务逻辑处理
     */
    private void login() {
        //1.获取输入的用户名和密码
        String registNmae = et_Register.getText().toString();
        String registPwd = et_Code.getText().toString();
        //2.校验输入的用户名和密码
        if (TextUtils.isEmpty(registNmae)||TextUtils.isEmpty(registPwd)){
            Toast.makeText(this, "输入的用户名或密码不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        //3.登录逻辑处理
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去环信服务器登陆
                EMClient.getInstance().login(registNmae, registPwd, new EMCallBack() {

                    //登录成功
                    @Override
                    public void onSuccess() {
                        // 对模型层数据的处理
                        Model.getInstance().logininSuccess(new UserInfo(registNmae));

                        // 保存用户账号信息到本地数据库
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(registNmae));
                        //提示登录成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                //跳转到主页面
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));

                                finish();
                            }
                        });
                    }

                    //登录失败
                    @Override
                    public void onError(int i, String s) {
                        //提示登录失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败"+s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //登录过程中的处理
                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    /**
     *注册的业务逻辑处理
     */
    private void regist() {
        //1.获取输入的用户名和密码
        String registNmae = et_Register.getText().toString();
        String registPwd = et_Code.getText().toString();
        //2.校验输入的用户名和密码
        if (TextUtils.isEmpty(registNmae)||TextUtils.isEmpty(registPwd)){
            Toast.makeText(this, "输入的用户名或密码不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        //3.去服务器注册账号
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //去环信服务器注册
                    EMClient.getInstance().createAccount(registNmae,registPwd);

                    //更新页面显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //更新页面显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //用户已经存在
                            if (e.toString().equals("com.hyphenate.exceptions.HyphenateException: User already exist")){
                                Toast.makeText(LoginActivity.this, "用户已经存在", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        tv_Register = findViewById(R.id.tv_register);
        et_Register = findViewById(R.id.et_register);
        tv_Code = findViewById(R.id.tv_code);
        et_Code =  findViewById(R.id.et_code);
        btn_Register =  findViewById(R.id.btn_register);
        btn_SignIn = findViewById(R.id.btn_signIn);
    }
}
