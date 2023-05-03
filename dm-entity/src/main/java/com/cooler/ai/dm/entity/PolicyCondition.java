package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyCondition {
    private Integer id;

    private Integer policyId;

    private String conditionName;

    private Byte conditionWhether;

    private String conditionText;

    private String domainName;

    private String taskName;

    private String version;

    private Byte enable;

    private String msg;


}