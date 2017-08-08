package com.cfysu.ssi.service;

import com.cfysu.ssi.model.Product;

/**
 * Created by cj on 2017/8/8.
 */
public interface ProductService {

    int updateProductStockById(Product product);

    Product queryProductById(Long skuId);
}
