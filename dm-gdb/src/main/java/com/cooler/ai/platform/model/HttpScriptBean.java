package com.cooler.ai.platform.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.swing.text.html.FormSubmitEvent.MethodType.POST;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpScriptBean {
    private String url;
    private String method;
    private String requestBody;
    private String responseName;

    private Integer socketTimeout;
    private Integer connectTimeout;
    private Integer connectionRequestTimeout;



    public static void main(String[] args) {

        JSONObject jo = new JSONObject();
        jo.put("sentence", "$sentence$");
        jo.put("excludeTqIds", "%excludeTqIds%");

        HttpScriptBean httpScriptBean = new HttpScriptBean("http://localhost:8081/v1/burouter2/getTop1TqData", "post", jo.toJSONString(), "TQDataInfo", null, null, null);
        String s = JSONObject.toJSONString(httpScriptBean);

        System.out.println(s);
    }
}