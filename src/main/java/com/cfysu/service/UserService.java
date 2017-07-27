package com.cfysu.service;

import com.cfysu.model.User;

/**
 * Created by cj on 2017/7/27.
 */
public interface UserService {

    public User selectByPrimaryKey(Long primaryKey);

    public int insert(User user);
}
