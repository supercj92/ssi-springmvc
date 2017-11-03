package com.cfysu.ssi.service;

import com.cfysu.ssi.model.User;
import com.cfysu.ssi.util.PageResult;
import com.cfysu.ssi.util.PaginationInfo;

import java.util.List;

/**
 * Created by cj on 2017/7/27.
 */
public interface UserService {

    public User selectByPrimaryKey(Long primaryKey);

    public int insert(User user);

    public PageResult queryByPage(User user);

    public Integer getCount(User user);
}
