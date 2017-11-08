package com.cfysu.ssi.service.impl;

import com.cfysu.ssi.model.User;
import com.cfysu.ssi.dao.UserDao;
import com.cfysu.ssi.service.UserService;
import com.cfysu.ssi.util.PageResult;
import com.cfysu.ssi.util.PaginationInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public PageResult queryByPage(User user) {

        PageResult pageResult = new PageResult();

        Integer totalCount = userDao.getCount(user);

        User userQuery = new User(user.getPageNum(), totalCount, user.getPageSize());

        List<User> userList = userDao.queryByList(userQuery);

        pageResult.setResList(userList);

        PaginationInfo paginationInfo = new PaginationInfo(user.getPageNum(), totalCount);

        pageResult.setPaginationInfo(paginationInfo);

        return pageResult;
    }

    @Override
    public Integer getCount(User user) {
        return userDao.getCount(user);
    }
}
