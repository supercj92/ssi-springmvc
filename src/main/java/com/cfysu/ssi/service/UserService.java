package com.cfysu.ssi.service;

import com.cfysu.ssi.model.User;

/**
 * Created by cj on 2017/7/27.
 */
public interface UserService {

    public User selectByPrimaryKey(Long primaryKey);

    public int insert(User user);
}
