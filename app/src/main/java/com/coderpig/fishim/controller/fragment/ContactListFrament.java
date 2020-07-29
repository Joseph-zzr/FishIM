package com.coderpig.fishim.controller.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.coderpig.fishim.R;
import com.coderpig.fishim.controller.activity.AddContactActivity;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseContactListFragment;

import androidx.fragment.app.Fragment;

/**
 * 联系人列表页面
 */
public class ContactListFrament extends EaseContactListFragment implements View.OnClickListener {
    private LinearLayout ll_contact_invite;
    private LinearLayout ll_contact_group;

    @Override
    protected void initView() {
        super.initView();
        Toast.makeText(getActivity(), "联系人页面", Toast.LENGTH_SHORT).show();
        //布局显示加号
        titleBar.setRightImageResource(R.drawable.em_add);

        //头布局
        View headerView = View.inflate(getActivity(),R.layout.header_fragment_contact,null);
        listView.addHeaderView(headerView);

        ll_contact_invite =  headerView.findViewById(R.id.ll_contact_invite);
        ll_contact_group =headerView.findViewById(R.id.ll_contact_group);
        ll_contact_invite.setOnClickListener(this);
        ll_contact_group.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_contact_group:
                Toast.makeText(getActivity(), "群组被按下", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_contact_invite:
                Toast.makeText(getActivity(), "邀请被按下", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        //添加按钮的点击事件处理
        titleBar.setRightLayoutClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });
    }
}
