package com.cfysu.ssi.dao;

import com.alibaba.fastjson.JSON;
import com.cfysu.ssi.model.Product;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by cj on 2017/8/8.
 */
@ContextConfiguration(locations = "/spring-context-test.xml")
public class ProductDaoTest extends AbstractJUnit4SpringContextTests{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDaoTest.class);

    @Autowired
    private ProductDao productDao;

    @Test
    public void testQueryProduct(){
        Product product = productDao.queryForObject(1L);
        LOGGER.info("product:{}", JSON.toJSONString(product));
    }

    @Test
    public void testUpdateProductById(){
        Product product = new Product();
        product.setStock(11);
        product.setSkuId(1);
        productDao.updateStockById(product);
    }
}
