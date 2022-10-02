package com.cooler.ai.platform.facade.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DomainTaskData implements java.io.Serializable{
    private String sessionId = null;
    private int totalTurnNum = 0;
    Map<String, Integer> turnNumMap = new HashMap<>();                      //Map<domainName_taskId, turnNum1> + Map<domainName, turnNum2>

    public void increaseTurnNum(String domainName, String taskName){
        this.totalTurnNum ++;

        Integer domainTurnNum = this.turnNumMap.get(domainName);
        domainTurnNum = domainTurnNum != null ? domainTurnNum + 1 : 1;
        this.turnNumMap.put(domainName, domainTurnNum);

        String topic = domainName + "::" + taskName;
        Integer domainTaskTurnNum = this.turnNumMap.get(topic);
        domainTaskTurnNum = domainTaskTurnNum != null ? domainTaskTurnNum + 1 : 1;
        this.turnNumMap.put(topic, domainTaskTurnNum);
    }
}
