package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.PolicyActionMapper;
import com.cooler.ai.dm.entity.PolicyAction;
import com.cooler.ai.dm.service.PolicyActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("policyActionService")
public class PolicyActionServiceImpl implements PolicyActionService {
    @Autowired
    private PolicyActionMapper policyActionMapper;

    @Override
    public int insert(PolicyAction record) {
        return policyActionMapper.insert(record);
    }
}
