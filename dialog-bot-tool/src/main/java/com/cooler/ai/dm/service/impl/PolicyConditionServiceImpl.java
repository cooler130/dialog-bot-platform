package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.dao.PolicyConditionMapper;
import com.cooler.ai.dm.entity.PolicyCondition;
import com.cooler.ai.dm.service.PolicyConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("policyConditionService")
public class PolicyConditionServiceImpl implements PolicyConditionService {
    @Autowired
    private PolicyConditionMapper policyConditionMapper;

    @Override
    public int insert(PolicyCondition record) {
        return policyConditionMapper.insert(record);
    }
}
