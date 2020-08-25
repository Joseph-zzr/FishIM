package com.coderpig.fishim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.coderpig.fishim.R;
import com.coderpig.fishim.controller.adapter.PickContactAdapter;
import com.coderpig.fishim.model.Model;
import com.coderpig.fishim.model.bean.PickContactInfo;
import com.coderpig.fishim.model.bean.UserInfo;
import com.coderpig.fishim.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;


public class PickContactActivity extends Activity {

    private TextView tv_pick_save;
    private ListView lv_pick;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> mExistMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);

        //获取传递过来的数据
        getData();

        initView();

        initData();

        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId != null){
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);

            //获取群众已经存在的所有群成员
            mExistMembers = group.getMembers();
        }

        if (mExistMembers == null){
            mExistMembers = new ArrayList<>();
        }

    }

    private void initListener() {
        //listView条目的点击事件
        lv_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //checkBox 设置点击
                CheckBox checkBox = view.findViewById(R.id.cb_pick);
                checkBox.setChecked(!checkBox.isChecked());

                //修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setInChecked(checkBox.isChecked());

                //刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });

        //保存按钮的点击事件
        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取到已经选择的联系人
                List<String>  names = pickContactAdapter.getPickContacts();

                //给启动页面返回数据
                Intent intent = new Intent();

                intent.putExtra("members",names.toArray(new String[0]));

                //设置返回的结果码
                setResult(RESULT_OK,intent);

                //结束当前页面
                finish();
            }
        });
    }

    private void initData() {
        //从本地数据库中获取所有的联系人信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTabDao().getContacts();
        mPicks = new ArrayList<>();
        if(contacts != null && contacts.size() >= 0){
            for (UserInfo contact : contacts){
                PickContactInfo pickContactInfo = new PickContactInfo(contact,false);
                mPicks.add(pickContactInfo);
            }
        }

        //初始化lisiview
        pickContactAdapter = new PickContactAdapter(this,mPicks,mExistMembers);

        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initView() {
        tv_pick_save = findViewById(R.id.tv_pick_save);
        lv_pick = findViewById(R.id.lv_pick);
    }
}
