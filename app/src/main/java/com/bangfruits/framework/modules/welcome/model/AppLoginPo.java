package com.bangfruits.framework.modules.welcome.model;


import java.util.List;
import com.bangfruits.framework.common.enmu.*;

/**
 * Created by LY on 2019/4/2.
 */
public class AppLoginPo {

    private String token;
    private int id;
    private String name;
    private String userName;
    private String realName;
    private String email;
    private String openId;
    private String qq;
    private String mobilePhone;
    private UserType userType;
    private List<Integer> roleIds;
    private String roleNames;
    private String firstDepId;
    private String firstDepName;
    private String depName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getFirstDepId() {
        return firstDepId;
    }

    public void setFirstDepId(String firstDepId) {
        this.firstDepId = firstDepId;
    }

    public String getFirstDepName() {
        return firstDepName;
    }

    public void setFirstDepName(String firstDepName) {
        this.firstDepName = firstDepName;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
