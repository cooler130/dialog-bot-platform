package com.cooler.ai.platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConditionNodeRecord {
    private String SUID;
    private String domain;
    private String name;
    private String _neo4j_labels;

    private String type;
    private String param;
    private String option;
    private String value;

    private Boolean passed;
}
