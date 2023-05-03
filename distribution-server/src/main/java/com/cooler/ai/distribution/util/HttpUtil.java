package com.cooler.ai.distribution.util;

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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static Integer defaultSocketTimeout = 10000;
    private static Integer defaultConnectTimeout = 10000;
    private static Integer defaultConnectionRequestTimeout = 5000;
    private static String charset = "utf-8";

    //------------------------------------------------------------------------------------------GET请求
    /**
     * 普通HTTP的GET请求
     * @param url
     * @param requestParamMap
     * @param httpHeaders
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, String> requestParamMap, Map<String, String> httpHeaders) throws Exception{
        return doGetWithTime(url, requestParamMap, httpHeaders, null, null, null);
    }

    /**
     * REST-HTTP的GET请求
     * @param url
     * @param requestParams
     * @param httpHeaders
     * @return
     * @throws Exception
     */
    public static String doGet4Rest(String url, List<String> requestParams, Map<String, String> httpHeaders) throws Exception{
        return doGet4RestWithTime(url, requestParams, httpHeaders, null, null, null);
    }

    public static String doGetWithTime(String url, Map<String, String> requestParamMap, Map<String, String> httpHeaders, Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);                                            // 定义请求的参数
        if(requestParamMap != null && requestParamMap.size() > 0){
            for (String param : requestParamMap.keySet()) {
                uriBuilder.addParameter(param, requestParamMap.get(param));
            }
        }
        URI uri = uriBuilder.build();
        return sendURI(uri, httpHeaders, socketTimeout, connectTimeout, connectionRequestTimeout);
    }


    public static String doGet4RestWithTime(String url, List<String> requestParams, Map<String, String> httpHeaders,
                                            Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws Exception {
        for (String requestParam : requestParams) {
            url += "/" + requestParam;
        }
        URIBuilder uriBuilder = new URIBuilder(url);                                            // 定义请求的参数
        URI uri = uriBuilder.build();
        return sendURI(uri, httpHeaders, socketTimeout, connectTimeout, connectionRequestTimeout);
    }

    private static String sendURI(URI uri, Map<String, String> httpHeaders,
                                  Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();                           // 创建Httpclient对象
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

        CloseableHttpResponse response = null;                                                  //response 对象
        try {
            response = httpclient.execute(httpGet);                                             // 执行http get请求
            if (response.getStatusLine().getStatusCode() == 200) {                              // 判断返回状态是否为200
                String content = EntityUtils.toString(response.getEntity(), charset);
                return content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
        return null;
    }


    //------------------------------------------------------------------------------------------POST请求
    /**
     * HTTP的POST请求
     * @param url
     * @param requestJson
     * @param httpHeaders
     * @return
     * @throws Exception
     */
    public static String doPost(String url, String requestJson, Map<String, String> httpHeaders) throws Exception {
        return doPostWithTime(url, requestJson, httpHeaders, null, null, null);
    }

    public static String doPostWithTime(String url, String requestJson, Map<String, String> httpHeaders,
                                    Integer socketTimeout, Integer connectTimeout, Integer connectionRequestTimeout) throws Exception{
        String result = null;
        CloseableHttpClient httpClient = HttpClients.custom().build();

        HttpPost httpPost = new HttpPost(url);
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(socketTimeout != null ? socketTimeout : defaultSocketTimeout)
                .setConnectTimeout(connectTimeout != null ? connectTimeout : defaultConnectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout != null ? connectionRequestTimeout : defaultConnectionRequestTimeout)
                .build();
        httpPost.setConfig(config);
        try {
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            if(null != httpHeaders && httpHeaders.size() > 0){
                for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpPost.setEntity(new StringEntity(requestJson, charset));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            if(response == null || response.getStatusLine().getStatusCode() != 200){
                String errMsg = "发送HTTP请求后返回报文为空或HTTP状态码不为200，错误码：（" + response.getStatusLine().getStatusCode() + "）";
                logger.error(errMsg);
                throw new Exception(errMsg);
            }

            HttpEntity respEntity = response.getEntity();
            if(respEntity != null){
                result = EntityUtils.toString(respEntity, charset);
            }
            EntityUtils.consume(respEntity);

        } catch (IOException e){
            String errMsg = "Post响应解析失败";
            logger.error(errMsg, e);
            throw new Exception(errMsg, e);
        } catch (Exception ex){
            String errMsg = "Post请求失败";
            logger.error(errMsg, ex);
            throw new Exception(errMsg, ex);
        } finally {
            httpClient.close();
        }
        return result;
    }

}
