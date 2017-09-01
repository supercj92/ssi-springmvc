package com.cfysu.ssi.dao;

import com.cfysu.ssi.model.Order;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * Created by cj on 2017/8/8.
 */
@ContextConfiguration(locations = {"classpath:context/applicationContext.xml"})
public class OrderDaoTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private JedisPool jedisPool;

    @Test
    public void testInsert(){
        Order order = new Order();
        order.setCustomerName("cj");
        order.setNum(1);
        order.setSkuId(123456);
        orderDao.insert(order);
    }

    @Test
    public void testJedis(){
        Jedis jedis = jedisPool.getResource();
        System.out.println("hello " + jedis.get("hello"));
    }

}
