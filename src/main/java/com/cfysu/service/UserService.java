package com.cfysu.service;

import com.cfysu.model.User;
import com.cfysu.sqlmap.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by cj on 2017/6/8.
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public User selectByPrimaryKey(Integer primaryKey){
        return userMapper.selectByPrimaryKey(primaryKey);
    }

    @Transactional
    public int insert(User user){
        userMapper.insert(user);
        throw new RuntimeException();
        //return -1;
    }
}
