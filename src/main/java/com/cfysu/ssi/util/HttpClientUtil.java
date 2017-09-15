package com.cfysu.ssi.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by cj on 2017/8/9.
 */
public class HttpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
    /**
     * 发送GET请求
     * @param url getURL
     * @param params 请求参数
     * @param charset 编码
     * @return
     */
    public static String get(String url, Map<String,String> params, String charset) {

        String responseBody = null;

        HttpClient httpCilent = new HttpClient();
        GetMethod getMethod = new GetMethod(url);

        try {
            int index = 0;
            NameValuePair[] param = new NameValuePair[params.size()];
            for (Map.Entry<String, String> entry : params.entrySet()) {
                param[index] = new NameValuePair(entry.getKey(), entry.getValue());
                index++;
            }
            getMethod.setQueryString(param);

            httpCilent.executeMethod(getMethod);
            if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
                responseBody = getMethod.getResponseBodyAsString();
            }
        } catch (Exception e) {
            LOGGER.error("get请求提交失败:" + url, e);
        } finally {
            getMethod.releaseConnection();
        }

        return responseBody;
    }
}
