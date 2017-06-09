package com.cfysu.service;

import com.cfysu.model.TbUser;
import com.cfysu.sqlmap.TbUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by longcangjian on 2017/6/8.
 */
@Service
public class UserService {
    @Resource
    private TbUserMapper tbUserMapper;

    public TbUser selectByPrimaryKey(Integer primaryKey){
        return tbUserMapper.selectByPrimaryKey(primaryKey);
    }

}
