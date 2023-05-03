package com.cooler.ai.distribution.dao;

import com.cooler.ai.distribution.entity.DataVersion;
import org.apache.ibatis.annotations.Param;

public interface DataVersionMapper {
    /**
     * 下线 domainName和taskName 的不稳定的版本（
     * @param domainName
     * @param taskName
     * @return
     */
    Integer offlineUnstableVersion(@Param("domainName") String domainName, @Param ("taskName") String taskName);

    /**
     * 查找 domainName和taskName 最近一版
     * @param domainName
     * @param taskName
     * @return
     */
    DataVersion selectLatestVersion(@Param("domainName") String domainName, @Param ("taskName") String taskName);

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
    DataVersion selectLatestStableVersion(@Param("domainName") String domainName, @Param ("taskName") String taskName);

    /**
     * 查找指定的版本
     * @param domainName
     * @param taskName
     * @param versionName
     * @return
     */
    DataVersion selectOneVersion(@Param("domainName") String domainName, @Param ("taskName") String taskName, @Param ("versionName") String versionName);
}