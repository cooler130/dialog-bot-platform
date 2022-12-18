package com.cooler.ai.platform.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.model.HttpResult;
import com.cooler.ai.platform.model.HttpScriptBean;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static Integer defaultSocketTimeout = 10000;
    private static Integer defaultConnectTimeout = 10000;
    private static Integer defaultConnectionRequestTimeout = 5000;
    private static String charset = "utf-8";

    private static final String GET = "get";
    private static final String GET_REST = "get_rest";
    private static final String POST = "post";
    //------------------------------------------------------------------------------------------GET请求


    /**
     * 普通HTTP的GET请求
     * @param url
     * @param requestMapJS     装有参数Map的json字符串
     * @param httpHeaders
     * @return
     * @throws Exception
     */
    public static String doGet(String url, String requestMapJS, Map<String, String> httpHeaders,
                                       Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);                                            // 定义请求的参数
        Map<String, String> requestParamMap = JSON.parseObject(requestMapJS, Map.class);
        if(requestParamMap != null && requestParamMap.size() > 0){
            for (String param : requestParamMap.keySet()) {
                uriBuilder.addParameter(param, requestParamMap.get(param));
            }
        }
        URI uri = uriBuilder.build();

        HttpGet httpGet = new HttpGet(uri);                                                     // 创建http GET请求
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(socketTimeout != null ? socketTimeout : defaultSocketTimeout)
                .setConnectTimeout(connectTimeout != null ? connectTimeout : defaultConnectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout != null ? connectionRequestTimeout : defaultConnectionRequestTimeout)
                .build();
        httpGet.setConfig(config);
        if(null != httpHeaders && httpHeaders.size() > 0){
            for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return sendURI(GET, httpGet, null);
    }

    /**
     * REST-HTTP的GET请求
     * @param url
     * @param requestListJS    装有参数List的Json字符串
     * @param httpHeaders
     * @return
     * @throws Exception
     */
    public static String doGet4Rest(String url, String requestListJS, Map<String, String> httpHeaders,
                                            Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws URISyntaxException {
        List<String> requestParams = JSON.parseObject(requestListJS, List.class);
        if(requestParams != null && requestParams.size() > 0){
            for (String requestParam : requestParams) {
                url += "/" + requestParam;
            }
        }
        URIBuilder uriBuilder = new URIBuilder(url);                                            // 定义请求的参数
        URI uri = uriBuilder.build();

        HttpGet httpGet = new HttpGet(uri);                                                     // 创建http GET请求
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(socketTimeout != null ? socketTimeout : defaultSocketTimeout)
                .setConnectTimeout(connectTimeout != null ? connectTimeout : defaultConnectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout != null ? connectionRequestTimeout : defaultConnectionRequestTimeout)
                .build();
        httpGet.setConfig(config);
        if(null != httpHeaders && httpHeaders.size() > 0){
            for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return sendURI(GET_REST, httpGet, null);
    }

    /**
     * HTTP的POST请求
     * @param url
     * @param requestJson
     * @param httpHeaders
     * @return
     * @throws Exception
     */
    public static String doPost(String url, String requestJson, Map<String, String> httpHeaders,
                                Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);                                            // 定义请求的参数
        URI uri = uriBuilder.build();
        HttpPost httpPost = new HttpPost(uri);
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(socketTimeout != null ? socketTimeout : defaultSocketTimeout)
                .setConnectTimeout(connectTimeout != null ? connectTimeout : defaultConnectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout != null ? connectionRequestTimeout : defaultConnectionRequestTimeout)
                .build();
        httpPost.setConfig(config);

        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        if(null != httpHeaders && httpHeaders.size() > 0){
            for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        httpPost.setEntity(new StringEntity(requestJson, charset));
        return sendURI(POST,null, httpPost);
    }

    private static String sendURI(String method, HttpGet httpGet, HttpPost httpPost) {
        CloseableHttpClient httpclient = HttpClients.createDefault();                           // 创建Httpclient对象
        CloseableHttpResponse response = null;                                                  //response 对象
        try {
            if(method.equals(GET) || method.equals(GET_REST)){
                response = httpclient.execute(httpGet);
            }else if(method.equals(POST)){
                response = httpclient.execute(httpPost);
            }
            if (response.getStatusLine().getStatusCode() == 200) {                              // 判断返回状态是否为200
                HttpEntity respEntity = response.getEntity();
                String content = EntityUtils.toString(respEntity, charset);                     //content结构为：{"code": 200, "message": "Success","data": 对象字符串 }
                EntityUtils.consume(respEntity);
                HttpResult httpResult = JSON.parseObject(content, HttpResult.class);
                if(httpResult.getCode() == 200){
                    Object data = httpResult.getData();
                    return data == null ? null : JSON.toJSONString(data);
                } else {
                    String errMsg = "发送HTTP请求后，请求正常，返回HTTP状态码不为200，业务错误码：（" + httpResult.getCode() + "）";
                    logger.error(errMsg);
                }
            }else{
                String errMsg = "发送HTTP请求后，出现服务器或网络问题，HTTP状态码，http错误码：（" + response.getStatusLine().getStatusCode() + "）";
                logger.error(errMsg);
            }
        } catch (IOException e){
            String errMsg = "Get响应解析失败";
            logger.error(errMsg, e);
        } catch (Exception ex){
            String errMsg = "Get请求失败";
            logger.error(errMsg, ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                    httpclient.close();
                } catch (IOException e) { }
            }
        }
        return null;
    }


    //------------------------------------------------------------------------------------------POST请求



    /**
     * 执行http动作，并将httpParams变量值输出
     * @param actionContent     http调用数据
     * @param globalParams      全局变量池
     * @param localParams       局部变量池
     * @return  返回一个Map，Map的key为业务名称，而value格式不定
     */
    /**
     * 执行http动作，并将httpParams变量值输出
     * @param actionContent     http调用数据
     * @param pps
     * @param cps
     * @param sps
     * @param bps   业务变量池
     * @param lps   局部变量池
     * @return  返回一个Map，Map的key为业务名称，而value格式不定
     */
    public static Map<String, String> runHttpAction(String actionContent,
                                                    Map<String, String> pps, Map<String, String> cps,
                                                    Map<String, String> sps, Map<String, String> bps,
                                                    Map<String, String> lps) {
        if(actionContent == null || actionContent.length() == 0) return null;

        HttpScriptBean httpScriptBean = JSONObject.parseObject(actionContent, HttpScriptBean.class);
        String url = httpScriptBean.getUrl();
        String method = httpScriptBean.getMethod();                             //method: get/get_rest/post
        String requestBodyJS = httpScriptBean.getRequestBody();                 //get:Map<String, String> / get_rest:List<String> / post:JS
        String responseName = httpScriptBean.getResponseName();
        Integer socketTimeout = httpScriptBean.getSocketTimeout();
        Integer connectTimeout = httpScriptBean.getConnectTimeout();
        Integer connectionRequestTimeout = httpScriptBean.getConnectionRequestTimeout();

        requestBodyJS = StringUtil.replaceVariableValues(requestBodyJS, pps, cps, sps, bps, lps);                     //全局和局部变量值替代requestBody中的变量
        try {
            String responseRes = null;
            if(method.equals(POST)){
                responseRes = doPost(url, requestBodyJS, null, socketTimeout, connectTimeout, connectionRequestTimeout);
            }else if(method.equals(GET)){
                responseRes = doGet(url, requestBodyJS, null, socketTimeout, connectTimeout, connectionRequestTimeout);
            }else if (method.equals(GET_REST)){
                responseRes = doGet4Rest(url, requestBodyJS, null, socketTimeout, connectTimeout, connectionRequestTimeout);
            }

            if(responseRes != null){                                    //这里默认得到数据就是一个JSON或JSONArray字符串，是哪个由responseName来判断，由使用者根据接口返回值判断。
                Map<String, String> httpParams = new HashMap<>();
                httpParams.put("%" + responseName + "%", responseRes);          //这里取出来的都做为业务变量
                return httpParams;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
