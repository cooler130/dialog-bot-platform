package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Param {
    private Integer id;

    private String domainName;

    private String taskName;

    private String paramName;

    private Integer acquireType;

    private Integer groupNum;

    private String acquireContent;

    private Integer enable;

    private String msg;

}