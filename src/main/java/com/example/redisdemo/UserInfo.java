package com.example.redisdemo;


import java.io.Serializable;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = -6702944011110757819L;
    private int userNo;

    private String userName;

    private String userAddr;

    private String userEmail;

    private String createTime;

    public UserInfo() {
    }

    public UserInfo(int userNo, String userName, String userAddr, String userEmail) {
        this.userNo = userNo;
        this.userName = userName;
        this.userAddr = userAddr;
        this.userEmail = userEmail;
        this.createTime = "2019-01-01";
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

