package com.cfysu.ssi.dao;

import com.cfysu.ssi.dao.BaseDao;
import com.cfysu.ssi.model.Product;

/**
 * Created by cj on 2017/8/8.
 */
public interface ProductDao extends BaseDao<Product> {
    int updateStockById(Product product);
}
