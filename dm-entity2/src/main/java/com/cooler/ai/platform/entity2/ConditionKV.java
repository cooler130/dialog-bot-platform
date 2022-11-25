package com.cooler.ai.platform.entity2;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionKV {
    private Integer id;

    private Integer policyId;

    private String conditionKey;

    private Integer relationship;

    private String conditionValue;

    private Integer groupNum;

    private Byte logicType;

    private Byte enable;

    private String msg;

}