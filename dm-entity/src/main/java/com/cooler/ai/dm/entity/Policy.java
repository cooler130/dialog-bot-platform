package com.cooler.ai.dm.entity;

public class Policy {
    private Integer id;

    private String policyName;

    private String domainName;

    private String taskName;

    private String fromState;

    private String intentNames;

    private String toState;

    private String version;

    private Byte enable;

    private String msg;

    public Policy(Integer id, String policyName, String domainName, String taskName, String fromState, String intentNames, String toState, String version, Byte enable, String msg) {
        this.id = id;
        this.policyName = policyName;
        this.domainName = domainName;
        this.taskName = taskName;
        this.fromState = fromState;
        this.intentNames = intentNames;
        this.toState = toState;
        this.version = version;
        this.enable = enable;
        this.msg = msg;
    }

    public Policy() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName == null ? null : policyName.trim();
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

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState == null ? null : fromState.trim();
    }

    public String getIntentNames() {
        return intentNames;
    }

    public void setIntentNames(String intentNames) {
        this.intentNames = intentNames == null ? null : intentNames.trim();
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState == null ? null : toState.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
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