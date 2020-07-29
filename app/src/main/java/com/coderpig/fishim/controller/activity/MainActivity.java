package com.coderpig.fishim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.coderpig.fishim.R;
import com.coderpig.fishim.controller.fragment.ChatFragment;
import com.coderpig.fishim.controller.fragment.ContactListFrament;
import com.coderpig.fishim.controller.fragment.SettingFragment;
import com.hyphenate.easeui.ui.EaseChatFragment;

public class MainActivity extends FragmentActivity {
    private FrameLayout fl_main;


    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFrament contactListFrament;
    private SettingFragment settingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();
        //初始化监听
        initListener();
    }

    private void initListener() {
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = chatFragment;
                switch (checkedId){
                    case R.id.rb_main_chat://会话列表
                        fragment = chatFragment;
                        break;
                    case R.id.rb_main_contact://联系人页面
                        fragment = contactListFrament;
                        break;
                    case R.id.rb_main_setting://设置页面
                        fragment = settingFragment;
                        break;
                }
                //实现fragment切换
                switchFragment(fragment);
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fl_main,fragment).commit();
    }

    private void initData() {
        //创建三个fragment对象
        chatFragment = new ChatFragment();
        contactListFrament = new ContactListFrament();
        settingFragment = new SettingFragment();
    }

    private void initView() {
        rg_main = findViewById(R.id.rg_main);
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
    }
}
