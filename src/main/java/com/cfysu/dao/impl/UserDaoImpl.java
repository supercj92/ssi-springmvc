package com.cfysu.dao.impl;

import com.cfysu.dao.BaseDao;
import com.cfysu.dao.UserDao;
import com.cfysu.model.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

/**
 * Created by cj on 2017/7/27.
 */
@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User, Long> implements UserDao ,InitializingBean{


    public void afterPropertiesSet() throws Exception {
        setNameSpace("User");
    }

}
