package com.cooler.ai.platform.model;

import lombok.Data;

@Data
public class StateNode {
    private String SUID;
    private String _cytoscape_network;
    private String _neo4j_label;
    private String name;
    private String title;
    private String msg;
}
