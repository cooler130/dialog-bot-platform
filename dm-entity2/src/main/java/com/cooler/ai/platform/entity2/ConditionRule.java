package com.cooler.ai.platform.entity2;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionRule {
    private Integer id;

    private String conditionName;

    private Integer paramType;

    private String paramName;

    private Integer taskId;

    private Integer ruleType;

    private Float beliefThreshold;

    private String checkValue;

    private Integer checkRelationType;

    private String checkFunctionCode;

    private Integer recommendId;

    private Integer enable;

    private String msg;

}