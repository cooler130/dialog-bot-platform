package com.cooler.ai.dm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateNode {
    private String SUID;

    private String domain;
    private String task;
    private String name;
    private String _neo4j_labels;

    private String clazz;
    private String value;
    private String text;
    private String msg;
}
