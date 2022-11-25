package com.cooler.ai.platform.entity2;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transition {
    private Integer id;

    private String transitionName;

    private Integer startStateId;

    private Integer endStateId;

    private String taskName;

    private String relatedIntentNames;

    private Integer enable;

    private String msg;

}