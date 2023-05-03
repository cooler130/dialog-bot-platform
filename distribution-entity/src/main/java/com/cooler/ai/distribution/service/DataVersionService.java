package com.cooler.ai.distribution.service;

import com.cooler.ai.distribution.entity.DataVersion;

public interface DataVersionService {
    /**
     * 下线 domainName和taskName 的不稳定的版本（
     * @param domainName
     * @param taskName
     * @return
     */
    Integer offlineUnstableVersion(String domainName, String taskName);

    /**
     * 查找 domainName和taskName 最近一版
     * @param domainName
     * @param taskName
     * @return
     */
    DataVersion selectLatestVersion(String domainName, String taskName);

    /**
     * 查找 domainName和taskName以及version的版本
     * @param domainName
     * @param taskName
     * @return
     */
    DataVersion selectOneVersion(String domainName, String taskName, String versionName);


    /**
     * 添加最新版
     * @param record
     * @return
     */
    Integer insert(DataVersion record);


    /**
     * 查找 domainName和taskName 最近的一个稳定版
     * @param domainName
     * @param taskName
     * @return
     */
    DataVersion selectLatestStableVersion(String domainName, String taskName);
}
