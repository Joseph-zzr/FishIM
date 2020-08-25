package com.coderpig.fishim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.coderpig.fishim.R;
import com.coderpig.fishim.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 创建新群
 */

public class NewsGroupActivity extends Activity {

    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox ck_newgroup_open;
    private CheckBox ck_newgroup_openInvite;
    private Button btn_newgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_group);

        initView();

        initListener();
    }

    private void initListener() {
        //创建按钮的点击事件处理
        btn_newgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsGroupActivity.this,PickContactActivity.class);

                //跳转到选中联系人界面
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //成功获取到联系人
        if (resultCode == RESULT_OK ){
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    //创建群
    private void createGroup(String[] members) {
        //群名称
        String groupName = et_newgroup_name.getText().toString();
        //群描述
        String groupDesc = et_newgroup_desc.getText().toString();


        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去服务器创建群
                /*
                参数一：群名称
                参数二：群描述
                参数三：群成员
                参数四：原因
                参数五：参数设置
                 */
                EMGroupOptions options = new EMGroupOptions();

                options.maxUsers = 200;//群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle = null;

                if (ck_newgroup_open.isChecked()){//公开
                    if (ck_newgroup_openInvite.isChecked()){// 开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else{
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else{
                    if (ck_newgroup_openInvite.isChecked()){// 开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else{
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }

                options.style = groupStyle;//创建群的类型
                try {
                    EMClient.getInstance().groupManager().createGroup(groupName,groupDesc,members,"申请加入",options);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewsGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();

                            //结束当前页面
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewsGroupActivity.this, "创建群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_newgroup_name = (EditText) findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = (EditText) findViewById(R.id.et_newgroup_desc);
        ck_newgroup_open = (CheckBox) findViewById(R.id.ck_newgroup_open);
        ck_newgroup_openInvite = (CheckBox) findViewById(R.id.ck_newgroup_openInvite);
        btn_newgroup = (Button) findViewById(R.id.btn_newgroup);
    }
}
