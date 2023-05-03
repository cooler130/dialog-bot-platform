package com.cooler.ai.distribution.entity;

import java.util.Date;

public class DataVersion {
    private Integer id;

    private String versionName;

    private Float versionCode;

    private String domainName;

    private String taskName;

    private Byte isOnline;

    private Byte isStable;

    private String versionUpdateItems;

    private Date createTime;

    private Date updateTime;

    public DataVersion(Integer id, String versionName, Float versionCode, String domainName, String taskName, Byte isOnline, Byte isStable, String versionUpdateItems, Date createTime, Date updateTime) {
        this.id = id;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.domainName = domainName;
        this.taskName = taskName;
        this.isOnline = isOnline;
        this.isStable = isStable;
        this.versionUpdateItems = versionUpdateItems;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public DataVersion() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName == null ? null : versionName.trim();
    }

    public Float getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Float versionCode) {
        this.versionCode = versionCode;
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

    public Byte getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Byte isOnline) {
        this.isOnline = isOnline;
    }

    public Byte getIsStable() {
        return isStable;
    }

    public void setIsStable(Byte isStable) {
        this.isStable = isStable;
    }

    public String getVersionUpdateItems() {
        return versionUpdateItems;
    }

    public void setVersionUpdateItems(String versionUpdateItems) {
        this.versionUpdateItems = versionUpdateItems == null ? null : versionUpdateItems.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}