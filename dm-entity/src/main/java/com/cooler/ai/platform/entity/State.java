package com.cooler.ai.platform.entity;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private Integer id;

    private String stateName;

    private Integer domainId;

    private String domain;

    private Integer stateState;

    private Integer enable;

    private String msg;

}