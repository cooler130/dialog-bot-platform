package com.cooler.ai.platform.entity2;

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

    private Byte enable;

    private String msg;


}