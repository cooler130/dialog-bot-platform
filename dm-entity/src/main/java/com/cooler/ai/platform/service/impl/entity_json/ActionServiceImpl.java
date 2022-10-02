package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.Action;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.ActionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("jsonActionService")
public class ActionServiceImpl implements ActionService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<Action> selectByActionIds(Set<Integer> actionIds) {

        List<Action> actions = new ArrayList<>();
        for (Integer actionId : actionIds) {
            String targetKey = "Action_" + actionId;
            String json = globalMap.get(targetKey);
            if(json != null){
                Action action = JSONObject.parseObject(json, Action.class);
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    public Action getActionById(Integer actionId) {
        String targetKey = "Action_" + actionId;
        String json = globalMap.get(targetKey);
        if(json != null){
            Action action = JSONObject.parseObject(json, Action.class);
            return action;
        }
        return null;
    }

    @Override
    public Action getActionByProcessCode(String processCode) {
        String targetKey = "Action_" + processCode;
        String json = globalMap.get(targetKey);
        if(json != null){
            Action action = JSONObject.parseObject(json, Action.class);
            return action;
        }
        return null;
    }
}
