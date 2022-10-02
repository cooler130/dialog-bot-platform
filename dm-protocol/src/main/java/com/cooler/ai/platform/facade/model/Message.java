package com.cooler.ai.platform.facade.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message implements java.io.Serializable{
    private String messageType;
    private String messageData;
    private String lastFromStateId;
    private String fromStateId;
    private String fromStateId2;
    private String intentCondition;
    private String toStateId;

    public Message(String messageType, String messageData){
        this.messageType = messageType;
        this.messageData = messageData;
    }
}
