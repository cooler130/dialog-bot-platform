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

}