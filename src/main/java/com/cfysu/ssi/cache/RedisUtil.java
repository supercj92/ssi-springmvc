package com.cfysu.ssi.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by cj on 2017/9/1.
 */
@Component
public class RedisUtil {

    @Autowired
    private  JedisPool jedisPool;

    public void set(String key, String value){
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.close();
    }

    public String get(String key){
        Jedis jedis = jedisPool.getResource();
        String val = jedis.get(key);
        jedis.close();
        return val;
    }
}
