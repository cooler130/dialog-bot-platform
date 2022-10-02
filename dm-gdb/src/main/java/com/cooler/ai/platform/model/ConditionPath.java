package com.cooler.ai.platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConditionPath {
    private String intentSetSUID;
    private String intentName;
    private List<ConditionNode> conditionNodes;
    private String endStateSUID;
    private String endStateName;

    private Boolean allPassed;
}
