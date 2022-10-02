package com.cooler.ai.platform.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionLogic {
    private Integer id;

    private String name;

    private Integer transitionId;

    private Integer conditionRuleId;

    private Integer logicType;

    private Byte nopassNotice;

    private Integer groupNum;

    private Integer enable;

    private String msg;


}