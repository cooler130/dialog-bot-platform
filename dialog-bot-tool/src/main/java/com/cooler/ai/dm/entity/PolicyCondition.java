package com.cooler.ai.dm.entity;

public class PolicyCondition {
    private Integer id;

    private Integer policyId;

    private String conditionName;

    private Byte conditionWhether;

    private String conditionText;

    private Byte enable;

    private String msg;

    public PolicyCondition(Integer id, Integer policyId, String conditionName, Byte conditionWhether, String conditionText, Byte enable, String msg) {
        this.id = id;
        this.policyId = policyId;
        this.conditionName = conditionName;
        this.conditionWhether = conditionWhether;
        this.conditionText = conditionText;
        this.enable = enable;
        this.msg = msg;
    }

    public PolicyCondition() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName == null ? null : conditionName.trim();
    }

    public Byte getConditionWhether() {
        return conditionWhether;
    }

    public void setConditionWhether(Byte conditionWhether) {
        this.conditionWhether = conditionWhether;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText == null ? null : conditionText.trim();
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