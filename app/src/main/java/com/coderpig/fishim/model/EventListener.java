package com.coderpig.fishim.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.INotificationSideChannel;
import android.view.Display;

import com.coderpig.fishim.controller.activity.SplashActivity;
import com.coderpig.fishim.model.bean.GroupInfo;
import com.coderpig.fishim.model.bean.InvitationInfo;
import com.coderpig.fishim.model.bean.UserInfo;
import com.coderpig.fishim.utils.Constant;
import com.coderpig.fishim.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 全局事件的监听类
 */

public class EventListener {
    private Context mContext;
    private LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;

        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);

        //注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(eEMGroupChangeListener);
    }
    private final EMGroupChangeListener eEMGroupChangeListener = new EMGroupChangeListener() {
        /**
         * @param s:groupId
         * @param s1:groupName
         * @param s2:inviter
         * @param s3:reason
         */

        //收到 群邀请
        @Override
        public void onInvitationReceived(String s, String s1, String s2, String s3) {
            // 数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(s3);
            invitationInfo.setGroup(new GroupInfo(s1,s,s2));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.NEW_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        //收到 群申请通知
        @Override
        public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(s3);
            invitationInfo.setGroup(new GroupInfo(s1,s,s2));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        //收到 群邀请被接受
        @Override
        public void onRequestToJoinAccepted(String s, String s1, String s2) {
            //更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(s1,s,s2));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        //收到 群邀请被拒绝
        @Override
        public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {
            //更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(s3);
            invitationInfo.setGroup(new GroupInfo(s1,s,s2));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationAccepted(String s, String s1, String s2) {
            //更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(s2);
            invitationInfo.setGroup(new GroupInfo(s,s,s1));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        //群邀请被拒绝
        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {
            //更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(s2);
            invitationInfo.setGroup(new GroupInfo(s,s,s1));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        //群成员删除
        @Override
        public void onUserRemoved(String s, String s1) {

        }

        //群解散
        @Override
        public void onGroupDestroyed(String s, String s1) {

        }

        //收到 群邀请被自动接收
        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2){
            //更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(s2);
            invitationInfo.setGroup(new GroupInfo(s,s,s1));
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGE));
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberJoined(String s, String s1) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    };
    private final EMContactListener emContactListener = new EMContactListener() {
        //联系人加入
        @Override
        public void onContactAdded(String hxid) {
            //数据库更新
            Model.getInstance().getDbManager().getContactTabDao().saveContact(new UserInfo(hxid),true);

            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

        }

        //联系人删除
        @Override
        public void onContactDeleted(String hxId) {
            //数据库更新
            Model.getInstance().getDbManager().getContactTabDao().deleteContactByHxId(hxId);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxId);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //接收到联系人的邀请
        @Override
        public void onContactInvited(String hxId, String reason) {
            // 数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new UserInfo(hxId));
            invitationInfo.setReason(reason);
            //新邀请
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.NEW_INVITE);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //朋友接收你的邀请并接受
        @Override
        public void onFriendRequestAccepted(String hxId) {
            //数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new UserInfo(hxId));
            //邀请被接收了
            invitationInfo.setStaus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //朋友拒绝拒绝好友邀请
        @Override
        public void onFriendRequestDeclined(String s) {
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

}
