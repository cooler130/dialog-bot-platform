package com.cooler.ai.distribution.facade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DMResponse implements Serializable{
    private int code;
    private String msg;
    private String dmName;
    private String sessionId;
    private int turnNum;
    private List<Message> data;
    private String dialogActName;                   //bot对话行为.
    private Map<String, String> extMap;             //扩展字段Map

    private long receiveTime;
    private long returnTime;

    @Override
    public String toString() {
        return "DMResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", dmName='" + dmName + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", turnNum='" + turnNum + '\'' +
                ", data=" + data +
                ", dialogActName='" + dialogActName + '\'' +
                ", extMap=" + extMap +
                ", receiveTime=" + receiveTime +
                ", returnTime=" + returnTime +
                '}';
    }
}
