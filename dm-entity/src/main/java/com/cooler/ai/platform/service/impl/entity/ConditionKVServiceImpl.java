package com.cooler.ai.platform.service.impl.entity;

import com.cooler.ai.platform.dao.ConditionKVMapper;
import com.cooler.ai.platform.entity.ConditionKV;
import com.cooler.ai.platform.service.entity.ConditionKVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Service("conditionKVService")
public class ConditionKVServiceImpl implements ConditionKVService {

    @Autowired
    private ConditionKVMapper conditionKVMapper;

    @Override
    public List<ConditionKV> getConditionKVsByPolicyIds(Set<Integer> policyIds) {
        return conditionKVMapper.getConditionKVsByPolicyIds(policyIds);
    }
}
