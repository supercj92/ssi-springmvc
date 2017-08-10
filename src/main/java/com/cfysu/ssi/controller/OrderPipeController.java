package com.cfysu.ssi.controller;

import com.cfysu.ssi.dao.ProductDao;
import com.cfysu.ssi.model.Order;
import com.cfysu.ssi.model.Product;
import com.cfysu.ssi.service.OrderService;
import com.cfysu.ssi.service.ProductService;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by cj on 2017/8/8.
 */
@Controller
@RequestMapping("/order")
public class OrderPipeController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/pipe/{customer}/{skuId}/{num}", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String pipeOrder(@PathVariable String customer, @PathVariable Integer skuId, @PathVariable Integer num){
        if(StringUtils.isEmpty(customer) || skuId == null || num ==null){
            return "入参错误";
        }
        Integer res = orderService.pipeOrder(customer, skuId, num);
        if(res.equals(1)){
            return "下单成功！下单时间：" + new Date();
        }else if (res.equals(-1)){
            return "库存不足！";
        }
        return null;
    }

    @RequestMapping("/pipeOrder")
    @ResponseBody
    public String pipeOrderUrlTradition(@RequestParam String customer, @RequestParam Integer skuId, @RequestParam Integer num){
        return pipeOrder(customer, skuId, num);
    }
}
