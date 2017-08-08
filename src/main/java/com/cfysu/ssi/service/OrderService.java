package com.cfysu.ssi.service;

import com.cfysu.ssi.model.Order;

/**
 * Created by cj on 2017/8/8.
 */
public interface OrderService {

    long insertOrder(Order order);

    Integer pipeOrder(String customerName, Integer skuId, Integer num);
}
