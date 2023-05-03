package com.cooler.ai.distribution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Intent {
    private Integer id;

    private String intentName;

    private String domainName;

    private String taskNames;

    private Integer intentType;

    private String version;

    private Byte enable;

    private String msg;


}