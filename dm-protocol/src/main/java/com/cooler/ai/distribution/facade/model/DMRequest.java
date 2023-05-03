package com.cooler.ai.distribution.facade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DMRequest implements Serializable {
    private ClientInfo clientInfo;                  //客户端信息
    private UserInfo userInfo;                      //账户信息
    private LocationInfo locationInfo;              //位置信息

    private String sessionId;                       //会话Id（它由客户端来产生和控制，进而让客户端来决定是否使用上下文）
    private String dmType;                          //DM类型（3种，rdb、gdb、tree，rdb基于关系型数据库实现的dm，较复杂，后面会淘汰；gdb基于图数据库实现；tree基于树实现)

    private String requestType;                     //请求类型（语音/文本请求类型；点击请求类型；）
    private String query;                           //请求的语句（如果是语音/文本请求类型）
    private Map<String, String> extendInfo;         //扩展信息（各类扩展变量可以放到里面）
    private String version;                         //请求的数据版本（选填，无果没有，则选择最近的稳定版）

}
