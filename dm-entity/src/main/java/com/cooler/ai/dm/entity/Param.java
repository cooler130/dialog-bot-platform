package com.cooler.ai.dm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Param implements Comparable<Param>{
    private Integer id;

    private String domainName;

    private String taskName;

    private String paramName;

    private Integer acquireType;

    private Integer groupNum;

    private String acquireContent;

    private String version;

    private Integer enable;

    private String msg;

    @Override
    public int compareTo(Param o) {
        if(this.groupNum > o.groupNum) return 1;
        else if(this.groupNum < o.groupNum) return -1;
        return 0;
    }
}