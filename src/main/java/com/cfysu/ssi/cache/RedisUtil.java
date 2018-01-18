package com.cfysu.ssi.cache;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by cj on 2017/9/1.
 */

//public class RedisUtil {
//
//    @Autowired
//    private  JedisPool jedisPool;
//
//    public void set(String key, String value){
//        Jedis jedis = jedisPool.getResource();
//        jedis.set(key, value);
//        jedis.close();
//    }
//
//    public String get(String key){
//        Jedis jedis = jedisPool.getResource();
//        String val = jedis.get(key);
//        jedis.close();
//        return val;
//    }
//}
