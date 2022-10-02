package com.cooler.ai.platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConditionNode {
    private String SUID;
    private String _cytoscape_network;
    private String _neo4j_label;
    private String name;
    private String title;

    private String type;
    private String param;
    private String option;
    private String value;

    private Boolean passed;
}
