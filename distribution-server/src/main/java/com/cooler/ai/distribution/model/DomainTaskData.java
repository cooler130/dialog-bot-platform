package com.cooler.ai.distribution.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DomainTaskData implements Serializable {
    private String sessionId = null;
    private int totalTurnNum = 0;
    private Map<String, DomainData> domainDataMap = new HashMap<>();

    /**
     * domain下的task增加一轮
     * @param domainName
     * @param taskName
     */
    public void increaseTurnNum(String domainName, String taskName){
        this.totalTurnNum ++;

        DomainData domainData = domainDataMap.get(domainName);
        if(domainData == null) domainData = new DomainData(domainName, 0, new HashMap<>());
        domainData.setDomainTurnNum(domainData.getDomainTurnNum() + 1);
        domainDataMap.put(domainName, domainData);

        Map<String, TaskData> taskDataMap = domainData.getTaskDataMap();
        TaskData taskData = taskDataMap.get(taskName);
        if(taskData == null) taskData = new TaskData(taskName, 0);
        taskData.setTaskTurnNum(taskData.getTaskTurnNum() + 1);
        taskDataMap.put(taskName, taskData);
    }

    /**
     * 获取domainName领域下的轮次
     * @param domainName
     * @return
     */
    public int getDomainTurnNum(String domainName){
        DomainData domainData = domainDataMap.get(domainName);
        if(domainData == null) return 0;
        return domainData.getDomainTurnNum();
    }

    /**
     * 获取domainName领域下taskName任务的轮次
     * @param domainName
     * @param taskName
     * @return
     */
    public int getTaskTurnNum(String domainName, String taskName){
        DomainData domainData = domainDataMap.get(domainName);
        if(domainData == null) return 0;
        Map<String, TaskData> taskDataMap = domainData.getTaskDataMap();
        TaskData taskData = taskDataMap.get(taskName);
        if(taskData == null) return 0;
        return taskData.getTaskTurnNum();
    }

    @Data
    @AllArgsConstructor
    public class DomainData implements Serializable{
        private String domainName;
        private int domainTurnNum;
        Map<String, TaskData> taskDataMap;
    }

    @Data
    @AllArgsConstructor
    public class TaskData implements Serializable{
        private String taskName;
        private int taskTurnNum;
    }
}
