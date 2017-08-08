package com.cfysu.ssi.dao;

import com.cfysu.ssi.model.Order;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;

/**
 * Created by cj on 2017/8/8.
 */
@ContextConfiguration(locations = {"/spring-context-test.xml"})
public class OrderDaoTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private OrderDao orderDao;

    @Test
    public void testInsert(){
        Order order = new Order();
        order.setCustomerName("cj");
        order.setNum(1);
        order.setSkuId(123456);
        orderDao.insert(order);
    }

}
