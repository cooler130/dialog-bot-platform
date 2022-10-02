package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.dao.ConditionKVMapper;
import com.cooler.ai.platform.entity.Action;
import com.cooler.ai.platform.entity.ConditionKV;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.ConditionKVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Service("jsonConditionKVService")
public class ConditionKVServiceImpl implements ConditionKVService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<ConditionKV> getConditionKVsByPolicyIds(Set<Integer> policyIds) {
        List<ConditionKV> conditionKVS = new ArrayList<>();
        for (Integer policyId : policyIds) {
            String targetKey = "ConditionKV_" + policyId;
            String json = globalMap.get(targetKey);
            if(json != null){
                ConditionKV conditionKV = JSONObject.parseObject(json, ConditionKV.class);
                conditionKVS.add(conditionKV);
            }
        }

        return conditionKVS;
    }
}
