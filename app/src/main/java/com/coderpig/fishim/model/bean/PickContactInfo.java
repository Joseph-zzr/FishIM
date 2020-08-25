package com.coderpig.fishim.model.bean;

/**
 * 选择联系人的bean类
 */

public class PickContactInfo {
    private UserInfo user;      //联系人
    private boolean inChecked;  //是否被选中的标记

    public PickContactInfo(UserInfo user, boolean inChecked) {
        this.user = user;
        this.inChecked = inChecked;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isInChecked() {
        return inChecked;
    }

    public void setInChecked(boolean inChecked) {
        this.inChecked = inChecked;
    }
}
