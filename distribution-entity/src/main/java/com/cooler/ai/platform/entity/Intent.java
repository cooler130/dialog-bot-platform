package com.cooler.ai.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Intent {
    private Integer id;

    private String intentName;

    private Integer domainId;

    private String domainName;

    private String taskNames;

    private Integer intentType;

    private String msg;

}