package com.cfysu.ssi.dao.impl;

import com.cfysu.ssi.dao.BaseDao;
import com.cfysu.ssi.dao.OrderDao;
import com.cfysu.ssi.model.Order;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

/**
 * Created by cj on 2017/8/8.
 */
@Repository("orderDao")
public class OrderDaoImpl extends BaseDaoImpl<Order> implements OrderDao, InitializingBean{
    public void afterPropertiesSet() throws Exception {
        setNameSpace("Order");
    }
}
