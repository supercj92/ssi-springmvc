package com.cfysu.ssi.util;

import com.cfysu.ssi.model.User;

import java.util.ArrayList;
import java.util.List;

public class PageResult {

    private PaginationInfo paginationInfo;

    private List<User> resList = new ArrayList<User>();

    public PaginationInfo getPaginationInfo() {
        return paginationInfo;
    }

    public void setPaginationInfo(PaginationInfo paginationInfo) {
        this.paginationInfo = paginationInfo;
    }

    public List<User> getResList() {
        return resList;
    }

    public void setResList(List<User> resList) {
        this.resList = resList;
    }
}
