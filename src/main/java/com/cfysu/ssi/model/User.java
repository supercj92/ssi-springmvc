package com.cfysu.ssi.model;

import com.cfysu.ssi.util.PaginationInfo;
import org.apache.commons.lang.StringUtils;

public class User extends PaginationInfo{

    private Integer id;

    private String userName;

    private String pwd;

    private String gender;

    private String contract;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

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