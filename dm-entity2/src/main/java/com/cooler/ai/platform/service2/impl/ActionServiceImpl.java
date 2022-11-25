package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.ActionMapper;
import com.cooler.ai.platform.entity2.Action;
import com.cooler.ai.platform.service2.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("actionService")
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionMapper actionMapper;

    @Override
    public List<Action> selectByActionIds(Set<Integer> actionIds) {
        return actionMapper.selectByActionIds(actionIds);
    }

    @Override
    public List<Action> getByPolicyId(Integer policyId) {
        return actionMapper.getByPolicyId(policyId);
    }

    @Override
    public Action getActionByProcessCode(String processCode) {
        return actionMapper.getActionByProcessCode(processCode);
    }


}
