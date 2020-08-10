package com.jjc.api.model.system.ext;

import com.jjc.api.model.system.SysUser;

/**
 * 用户登录类
 *
 * @author Allion.li
 * @Date 2020/7/29
 */
public class SysUserModify extends SysUser {

    private String currentUserId;  //当前登陆用户id
    private String ids; //id集合
    private String rePassword; //新密码

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }
}
