package com.cfysu.ssi.model;

import java.math.BigDecimal;

/**
 * Created by cj on 2017/8/8.
 */
public class Product {
    private Integer skuId;
    private String productName;
    private Integer stock;
    private BigDecimal price;

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
