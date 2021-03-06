package com.cfysu.ssi.dao.impl;

import com.cfysu.ssi.dao.BaseDao;
import com.cfysu.ssi.dao.UserDao;
import com.cfysu.ssi.model.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

/**
 * Created by cj on 2017/7/27.
 */
@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao ,InitializingBean{

    public void afterPropertiesSet() throws Exception {
        setNameSpace("User");
    }

}
