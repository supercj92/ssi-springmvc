package com.cfysu.ssi.rpc;

import com.test.hessian.service.HelloService;

/**
 * Created by cj on 2017/9/1.
 */
public class HessianServerServiceImpl implements HelloService{

    public String sayHello(String str) {
        return "this is a msg from server:" + str;
    }
}
