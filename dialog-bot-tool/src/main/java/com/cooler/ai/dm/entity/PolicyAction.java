package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyAction {
    private Integer id;

    private String actionName;

    private Integer policyId;

    private Integer actionType;

    private Integer groupNum;

    private String actionContent;

    private Integer enable;

    private String msg;

}