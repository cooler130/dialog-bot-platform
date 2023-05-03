package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformRelation {
    private Integer id;

    private String transformRelationName;

    private String domainName;

    private String taskName;

    private String contextState;

    private String intentNames;

    private String transformIntentName;

    private String version;

    private Byte enable;

    private String msg;

}