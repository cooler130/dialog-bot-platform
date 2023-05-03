package com.cooler.ai.dm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConditionPathRecord {
    private String startStateName;
    private String intentName;
    private String intentSetSUID;
    private List<ConditionNodeRecord> conditionNodeRecords;
    private String endStateName;

    private Boolean allPassed;



}
