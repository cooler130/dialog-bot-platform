package com.cooler.ai.dm.entity;

public class TransformRelation {
    private Integer id;

    private String transformRelationName;

    private String domainName;

    private String taskName;

    private String contextState;

    private String intentNames;

    private String transformIntentName;

    private Byte enable;

    private String msg;

    public TransformRelation(Integer id, String transformRelationName, String domainName, String taskName, String contextState, String intentNames, String transformIntentName, Byte enable, String msg) {
        this.id = id;
        this.transformRelationName = transformRelationName;
        this.domainName = domainName;
        this.taskName = taskName;
        this.contextState = contextState;
        this.intentNames = intentNames;
        this.transformIntentName = transformIntentName;
        this.enable = enable;
        this.msg = msg;
    }

    public TransformRelation() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransformRelationName() {
        return transformRelationName;
    }

    public void setTransformRelationName(String transformRelationName) {
        this.transformRelationName = transformRelationName == null ? null : transformRelationName.trim();
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName == null ? null : domainName.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getContextState() {
        return contextState;
    }

    public void setContextState(String contextState) {
        this.contextState = contextState == null ? null : contextState.trim();
    }

    public String getIntentNames() {
        return intentNames;
    }

    public void setIntentNames(String intentNames) {
        this.intentNames = intentNames == null ? null : intentNames.trim();
    }

    public String getTransformIntentName() {
        return transformIntentName;
    }

    public void setTransformIntentName(String transformIntentName) {
        this.transformIntentName = transformIntentName == null ? null : transformIntentName.trim();
    }

    public Byte getEnable() {
        return enable;
    }

    public void setEnable(Byte enable) {
        this.enable = enable;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg == null ? null : msg.trim();
    }
}