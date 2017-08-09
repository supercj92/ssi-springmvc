package com.cfysu.ssi.controller;

import com.alibaba.fastjson.JSONObject;
import com.cfysu.ssi.util.HttpClientUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cj on 2017/8/9.
 */
public class ConcurrentOrderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentOrderTest.class);
    private static String url = "http://127.0.0.1/order/pipe/cj/1/1";

    public static void main(String[] args){
        testConcurrentOrder();
    }

    public static void testConcurrentOrder(){
        for (int i = 0;i < 500;i++){
            new Thread(new Runnable() {
                public void run() {
                    String responseBody = HttpClientUtil.get(url, new HashMap<String, String>(), "utf-8");
                    LOGGER.info("responseBody:{}", responseBody);
                }
            }).start();
        }
    }
}
