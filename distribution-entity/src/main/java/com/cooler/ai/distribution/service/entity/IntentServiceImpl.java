package com.cooler.ai.distribution.service.entity;

import com.cooler.ai.distribution.dao.IntentMapper;
import com.cooler.ai.distribution.entity.Intent;
import com.cooler.ai.distribution.service.IntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("intentService")
public class IntentServiceImpl implements IntentService {

    @Autowired
    private IntentMapper intentMapper;


    @Override
    public Intent selectByIntentId(Integer intentId) {
        return intentMapper.selectByIntentId(intentId);
    }

    @Override
    public Intent selectByTwoNames(String domainName, String intentName) {
        return intentMapper.selectByTwoNames(domainName, intentName);
    }

    @Override
    public void insert(Intent intent) {
        intentMapper.insert(intent);
    }

    @Override
    public Integer selectMaxId() {
        return intentMapper.selectMaxId();
    }


}
