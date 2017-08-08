package com.cfysu.ssi.dao.impl;

import com.cfysu.ssi.dao.ProductDao;
import com.cfysu.ssi.model.Product;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

/**
 * Created by cj on 2017/8/8.
 */
@Repository("productDao")
public class ProductDaoImpl extends BaseDaoImpl<Product> implements ProductDao ,InitializingBean{

    public void afterPropertiesSet() throws Exception {
        setNameSpace("Product");
    }

    public int updateStockById(Product product){
       return sqlMapClientTemplate.update(nameSpace + ".updateStockById", product);
    }
}
