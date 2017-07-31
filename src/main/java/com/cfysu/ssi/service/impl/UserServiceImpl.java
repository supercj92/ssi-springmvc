package com.cfysu.ssi.service.impl;

import com.cfysu.ssi.model.User;
import com.cfysu.ssi.dao.UserDao;
import com.cfysu.ssi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by cj on 2017/6/8.
 */
@Service("userService")
public class UserServiceImpl implements UserService{
    @Resource
    private UserDao userDao;

    public User selectByPrimaryKey(Long primaryKey){
        return userDao.queryForObject(primaryKey);
    }

    @Transactional
    public int insert(User user){
        userDao.insert(user);
        throw new RuntimeException();
        //return -1;
    }
}
