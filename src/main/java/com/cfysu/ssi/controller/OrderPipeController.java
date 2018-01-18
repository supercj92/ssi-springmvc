package com.cfysu.ssi.controller;

import com.cfysu.ssi.service.OrderService;
import com.cfysu.ssi.service.ProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping(value = "/pipe/{customer}/{skuId}/{num}", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String pipeOrder(@PathVariable String customer, @PathVariable Integer skuId, @PathVariable Integer num){
        if(StringUtils.isEmpty(customer) || skuId == null || num ==null){
            return "入参错误";
        }
        Long res = null;
        try {
            res = orderService.pipeOrder(customer, skuId, num);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(res != null){
            return "订单id" + res+ "。下单时间：" + new Date();
        }else {
            return "库存不足！";
        }
    }

    @RequestMapping("/pipeOrder")
    @ResponseBody
    public String pipeOrderUrlTradition(@RequestParam String customer, @RequestParam Integer skuId, @RequestParam Integer num){
        return pipeOrder(customer, skuId, num);
    }

    @RequestMapping("/empty")
    public String empty(Model model){
        model.addAttribute("flag", "1");
        return "";
    }
}
