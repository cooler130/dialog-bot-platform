package com.cooler.ai.platform.entity2;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformRelation {
    private Integer id;

    private String transformRelationName;

    private Integer contextStateId;

    private String contextIntentNames;

    private String transformIntentName;

    private Byte enable;

    private String msg;

}