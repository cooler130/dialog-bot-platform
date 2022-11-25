package com.cooler.ai.platform.service.impl;

import com.cooler.ai.platform.dao.ActionMapper;
import com.cooler.ai.platform.entity.Action;
import com.cooler.ai.platform.service.entity.ActionService;
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
    public Action getActionById(Integer actionId) {
        return actionMapper.getActionById(actionId);
    }

    @Override
    public Action getActionByProcessCode(String processCode) {
        return actionMapper.getActionByProcessCode(processCode);
    }


}
