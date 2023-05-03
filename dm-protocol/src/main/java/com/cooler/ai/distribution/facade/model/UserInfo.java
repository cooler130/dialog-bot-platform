package com.cooler.ai.distribution.facade.model;

import lombok.Data;

@Data
public class UserInfo implements java.io.Serializable{
    private String userId;
    private String userName;
    private String token;
}
