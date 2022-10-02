package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.ConditionRuleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("jsonConditionRuleService")
public class ConditionRuleServiceImpl implements ConditionRuleService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<ConditionRule> selectByConditionRuleIds(Set<Integer> conditionRuleIds) {
        List<ConditionRule> conditionInfos = new ArrayList<>();
        for (Integer conditionRuleId : conditionRuleIds) {
            String json = globalMap.get("ConditionRule_" + conditionRuleId);
            if(json != null){
                ConditionRule conditionInfo = JSONObject.parseObject(json, ConditionRule.class);
                conditionInfos.add(conditionInfo);
            }
        }
        return conditionInfos;
    }

}
