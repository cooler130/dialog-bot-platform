package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.PolicyMapper;
import com.cooler.ai.dm.entity.Policy;
import com.cooler.ai.dm.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("policyService")
public class PolicyServiceImpl implements PolicyService {
    @Autowired
    private PolicyMapper policyMapper;

    @Override
    public int insert(Policy record) {
        return policyMapper.insert(record);
    }
}
