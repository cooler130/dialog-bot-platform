package com.cooler.ai.platform.service.framework.impl;

import com.cooler.ai.platform.dao2.DMEntityMapper;
import com.cooler.ai.platform.entity2.*;
import com.cooler.ai.platform.service.framework.DMEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dmEntityService")
public class DMEntityServiceImpl implements DMEntityService {

    @Autowired
    private DMEntityMapper DMEntityMapper;

    @Override
    public List<Action> selectAllAction() {
        return DMEntityMapper.selectAllAction();
    }

    @Override
    public List<ConditionKV> selectAllConditionKV() {
        return DMEntityMapper.selectAllConditionKV();
    }

    @Override
    public List<ConditionLogic> selectAllConditionLogic() {
        return DMEntityMapper.selectAllConditionLogic();
    }

    @Override
    public List<ConditionRule> selectAllConditionRule() {
        return DMEntityMapper.selectAllConditionRule();
    }

    @Override
    public List<Policy> selectAllPolicy() {
        return DMEntityMapper.selectAllPolicy();
    }

    @Override
    public List<State> selectAllState() {
        return DMEntityMapper.selectAllState();
    }

    @Override
    public List<Transition> selectAllTransition() {
        return DMEntityMapper.selectAllTransition();
    }
}
