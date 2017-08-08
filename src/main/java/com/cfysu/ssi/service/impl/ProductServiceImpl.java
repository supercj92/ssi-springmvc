package com.cfysu.ssi.service.impl;

import com.cfysu.ssi.dao.ProductDao;
import com.cfysu.ssi.model.Product;
import com.cfysu.ssi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cj on 2017/8/8.
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    public int updateProductStockById(Product product) {
        return productDao.updateStockById(product);
    }

    public Product queryProductById(Long skuId) {
        return productDao.queryForObject(skuId);
    }
}
