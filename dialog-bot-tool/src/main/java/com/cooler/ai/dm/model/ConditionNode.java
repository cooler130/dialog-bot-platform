package com.cooler.ai.dm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
