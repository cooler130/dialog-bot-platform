package com.cooler.ai.dm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class IntentSetNode2 {
    private String SUID;
    private String _cytoscape_network;
    private String _neo4j_label;
    private String name;
    private String title;
    private Set<String> intents;

}
