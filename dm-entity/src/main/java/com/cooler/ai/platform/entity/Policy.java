package com.cooler.ai.platform.entity;

import com.cooler.ai.platform.model.EntityConstant;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    private Integer id;

    private String policyName;

    private String intentName;

    private Integer stateId;

    private Integer startActionId;

    private Byte enable;

    private String msg;

    public static Policy DEFAULT_POLICY = new Policy(EntityConstant.DEFAULT_POLICY_ID, "默认兜底策略", EntityConstant.NO_INTENT, EntityConstant.END_STATE_ID, EntityConstant.DEFAULT_ACTION_ID, (byte)1, "默认兜底策略");



}