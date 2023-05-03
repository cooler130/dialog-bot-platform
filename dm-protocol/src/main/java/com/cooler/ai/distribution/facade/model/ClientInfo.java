package com.cooler.ai.distribution.facade.model;

import lombok.Data;

@Data
public class ClientInfo implements java.io.Serializable{
    private String clientId;
    private String clientName;
    private String clientType;
    private String channel;
}
