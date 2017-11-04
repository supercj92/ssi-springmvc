package com.cfysu.ssi.model;

import com.cfysu.ssi.util.PaginationInfo;

public class User extends PaginationInfo{

    private Integer id;

    private String userName;

    private String pwd;

    public User(){}

    public User(int pageNum, int totalCount, int pageSize){
        super(pageNum, totalCount, pageSize);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}