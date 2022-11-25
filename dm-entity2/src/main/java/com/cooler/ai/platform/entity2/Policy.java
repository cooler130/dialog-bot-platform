package com.cooler.ai.platform.entity2;

import com.cooler.ai.platform.model.EntityConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    private Integer id;

    private String policyName;

    private String domainName;

    private String taskName;

    private String fromState;

    private String intentNames;

    private String toState;

    private Byte enable;

    private String msg;

    public static Policy DEFAULT_POLICY = new Policy(
            EntityConstant.DEFAULT_POLICY_ID,
            "default_policy",
            null,
            null,
            EntityConstant.ANY_STATE,
            EntityConstant.ANY_INTENT,
            EntityConstant.END_STATE,
            (byte)1,
            "默认兜底策略"
    );
}