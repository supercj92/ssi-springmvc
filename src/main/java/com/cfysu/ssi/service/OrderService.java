package com.cfysu.ssi.service;

import com.cfysu.ssi.model.Order;

/**
 * Created by cj on 2017/8/8.
 */
public interface OrderService {

    long insertOrder(Order order);

    //注解事务
    Long pipeOrder(String customerName, Integer skuId, Integer num);
    //xml事务
    Long pipeOrder2(String customerName, Integer skuId, Integer num);
    //编程事务
    Long pipeOrder3(String customerName, Integer skuId, Integer num);
}
