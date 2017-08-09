package com.cfysu.ssi.service.impl;

import com.cfysu.ssi.dao.OrderDao;
import com.cfysu.ssi.model.Order;
import com.cfysu.ssi.model.Product;
import com.cfysu.ssi.service.OrderService;
import com.cfysu.ssi.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * Created by cj on 2017/8/8.
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ProductService productService;

    public long insertOrder(Order order) {
        return orderDao.insert(order);
    }

    //同时只允许一个线程操作
    @Transactional
    public synchronized Integer pipeOrder(String customerName, Integer skuId, Integer num) {
        Product product = productService.queryProductById(Long.valueOf(skuId));
        if(num > product.getStock()){
            LOGGER.info("库存不足!下单数量：{}，库存：{}",num,product.getStock());
            return -1;
        }
        //1.生成订单
        Order order = new Order();
        order.setSkuId(skuId.intValue());
        order.setNum(num);
        order.setCustomerName(customerName);
        //持久化
        insertOrder(order);

        //2.减库存
        product.setStock(product.getStock() - num);
        //持久化
        productService.updateProductStockById(product);

        return 1;
    }

    //TODO  使用编程式事务实现
}
