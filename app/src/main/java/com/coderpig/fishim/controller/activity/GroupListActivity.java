package com.coderpig.fishim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.coderpig.fishim.R;
import com.coderpig.fishim.controller.adapter.GroupListAdapter;
import com.coderpig.fishim.controller.adapter.GroupRcyclerviewAdapter;
import com.coderpig.fishim.controller.view.WrapRecyclerView;
import com.coderpig.fishim.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * 群组的联系人页面
 */

public class GroupListActivity extends Activity {
    private ListView rc_grouplist;

    private GroupListAdapter adapter;
    private LinearLayout ll_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();
    }

    private void initListener() {
        //recyclerview点击事件
        rc_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 ) return ;
                Intent intent = new Intent(GroupListActivity.this, CharActivity.class);

                //传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);

                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                //群id
                intent.putExtra(EaseConstant.EXTRA_USER_ID,  emGroup.getGroupId());
                startActivity(intent);

            }
        });

        //跳转到新建群
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this,NewsGroupActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initData() {
        adapter = new GroupListAdapter(this);
        rc_grouplist.setAdapter(adapter);

        //从环信服务器中获取所有群的信息
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络获取数据
                    List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();

                            adapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        rc_grouplist = findViewById(R.id.rc_grouplist);

        //添加头布局
        View headerview = View.inflate(this,R.layout.header_grouplist,null);
        rc_grouplist.addHeaderView(headerview);

        ll_grouplist = headerview.findViewById(R.id.ll_grouplist);

    }

    //刷新
    private void refresh(){
        adapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
