package com.coderpig.fishim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.coderpig.fishim.R;
import com.coderpig.fishim.controller.adapter.GroupDetailAdapter;
import com.coderpig.fishim.model.Model;
import com.coderpig.fishim.model.bean.GroupInfo;
import com.coderpig.fishim.model.bean.UserInfo;
import com.coderpig.fishim.utils.Constant;
import com.hyphenate.chat.EMCDNCanvas;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * 群详情页面
 */
public class GroupDetailActivity extends Activity {

    private GridView gv_groupdetail;
    private Button bt_groupdetail_out;
    private EMGroup emGroup;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers(UserInfo userInfo) {
            //跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this,PickContactActivity.class);

            //传递群id
            intent.putExtra(Constant.GROUP_ID,emGroup.getGroupId());

            startActivityForResult(intent,2);

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
//                    EMClient.getInstance().groupManager().addUsersToGroup(emGroup.getGroupId(),);
                }
            });
        }

        //删除群成员方法
        @Override
        public void onDeleteMember(UserInfo userInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //从环信服务器中删除此人
                        EMClient.getInstance().groupManager().removeUserFromGroup(emGroup.getGroupId(),userInfo.getHxid());

                        //更新页面
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private ArrayList<UserInfo> mUser;
    private GroupDetailAdapter groupDetailAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            //获取返回的准备要求的群成员信息
            String[] memberses = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(emGroup.getGroupId(),memberses);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();

        getData();

        initData();

        initListener();
    }

    private void initListener() {
        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    //判断当前是否是删除模式，如果是删除模式
                    case MotionEvent.ACTION_DOWN:
                        if (groupDetailAdapter.ismIsDeleteModel()){
                            //切换为非删除模式
                            groupDetailAdapter.setmIsDeleteModel(false);
                            //刷洗页面
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                }

                return false;
            }
        });
    }

    private void initData() {
        //初始化button显示
        initButtonDisplay();

        //初始化gridview
        initGridview();

        //从环形服务器获取所有的群成员
        getMembersFromHxServer();
    }

    private void getMembersFromHxServer() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器获取多有的群成员信息
                    EMClient.getInstance().groupManager().getGroupFromServer(emGroup.getGroupId());
                    List<String> members = emGroup.getMembers();

                    if (members != null && members.size()>=0){

                        mUser = new ArrayList<UserInfo>();
                        //转换
                        for (String member:members){
                            UserInfo userInfo = new UserInfo(member);
                            mUser.add(userInfo);
                        }
                    }

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新适配器
                            groupDetailAdapter.refresh(mUser);
                        }
                    });


                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridview() {
        //当前用户是群组 || 群公开了
        boolean isCanModify = EMClient.getInstance()
                                      .getCurrentUser()
                                      .equals(emGroup.getOwner()) || emGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this,isCanModify,mOnGroupDetailListener);

        gv_groupdetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {

        //判断当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(emGroup.getOwner())){
            //群主
            bt_groupdetail_out.setText("解散群");

            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(emGroup.getGroupId());

                                //发送退群的广播
                                exitGroupBroatCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else{
            //群成员
            bt_groupdetail_out.setText("退群");

            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //告诉环信服务器退群
                        EMClient.getInstance().groupManager().leaveGroup(emGroup.getGroupId());

                        //发送退群广播
                        exitGroupBroatCast();

                        //更新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "退群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 发送退群和解散群广播
     */
    private void exitGroupBroatCast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);

        intent.putExtra(Constant.GROUP_ID,emGroup.getGroupId());

        localBroadcastManager.sendBroadcast(intent);
    }

    //获取全部数据
    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);

        if (groupId == null){
            return;
        }else{
            emGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    private void initView() {
        gv_groupdetail = findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out = findViewById(R.id.bt_groupdetail_out);
    }
}
