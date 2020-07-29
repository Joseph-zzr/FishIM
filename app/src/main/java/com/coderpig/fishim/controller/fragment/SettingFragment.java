package com.coderpig.fishim.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.coderpig.fishim.R;
import com.coderpig.fishim.controller.activity.LoginActivity;
import com.coderpig.fishim.controller.activity.SplashActivity;
import com.coderpig.fishim.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 设置页面
 */
public class SettingFragment extends Fragment {
    private Button btn_setting_out;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting,null);
        
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_setting_out=view.findViewById(R.id.btn_setting_out);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        //1.在button上显示当前用户名称
        btn_setting_out.setText("退出登录("+ EMClient.getInstance().getCurrentUser() +")");
        //2.退出登录的逻辑处理
        btn_setting_out.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //登录环形服务器 退出当前用户
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //退出回到登录页面
                                        startActivity(new Intent(getActivity(),LoginActivity.class));
                                        getActivity().finish();
                                        //更新UI显示
                                        Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }

                            @Override
                            public void onError(int i, String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出失败"+s, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }
                });
            }
        });
    }
}
