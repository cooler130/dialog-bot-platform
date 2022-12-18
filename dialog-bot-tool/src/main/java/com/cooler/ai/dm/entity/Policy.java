package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {
    private Integer id;

    private String policyName;

    private String domainName;

    private String taskName;

    private String fromState;

    private String intentNames;

    private String toState;

    private Byte enable;

    private String msg;


}