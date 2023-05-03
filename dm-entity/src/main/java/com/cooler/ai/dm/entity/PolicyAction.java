package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyAction implements Comparable<PolicyAction>{
    private Integer id;

    private String actionName;

    private Integer policyId;

    private Integer actionType;

    private Integer groupNum;

    private String actionContent;

    private String domainName;

    private String taskName;

    private String version;

    private Integer enable;

    private String msg;


    @Override
    public int compareTo(PolicyAction o) {
        if(this.groupNum > o.groupNum) return 1;
        else if(this.groupNum < o.groupNum) return -1;
        return 0;
    }
}