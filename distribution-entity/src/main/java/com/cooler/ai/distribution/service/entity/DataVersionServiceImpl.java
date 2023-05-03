package com.cooler.ai.distribution.service.entity;

import com.cooler.ai.distribution.dao.DataVersionMapper;
import com.cooler.ai.distribution.entity.DataVersion;
import com.cooler.ai.distribution.service.DataVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dataVersionService")
public class DataVersionServiceImpl implements DataVersionService {

    @Autowired
    private DataVersionMapper dataVersionMapper;

    @Override
    public Integer offlineUnstableVersion(String domainName, String taskName) {
        int result = dataVersionMapper.offlineUnstableVersion(domainName, taskName);
        return result;
    }

    @Override
    public DataVersion selectLatestVersion(String domainName, String taskName) {
        return dataVersionMapper.selectLatestVersion(domainName, taskName);
    }

    @Override
    public DataVersion selectOneVersion(String domainName, String taskName, String versionName) {
        return dataVersionMapper.selectOneVersion(domainName, taskName, versionName);
    }

    @Override
    public Integer insert(DataVersion record) {
        return dataVersionMapper.insert(record);
    }

    @Override
    public DataVersion selectLatestStableVersion(String domainName, String taskName) {
        return dataVersionMapper.selectLatestStableVersion(domainName, taskName);
    }
}
