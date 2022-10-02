package com.cooler.ai.platform.service.impl.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.TransformRelation;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.service.entity.TransformRelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("jsonTransformRelationService")
public class TransformRelationServiceImpl implements TransformRelationService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public TransformRelation selectByContextStateIdIntent(Integer contextStateId, String contextIntentName) {
        List<TransformRelation> transformRelations = null;
        String json = globalMap.get("TransformRelation_" + contextStateId);
        if(json != null){
            transformRelations = JSONObject.parseArray(json, TransformRelation.class);
            if(transformRelations != null && transformRelations.size() > 0){
                for (TransformRelation transformRelation : transformRelations) {
                    String contextIntentNames = transformRelation.getContextIntentNames();
                    String[] splits = contextIntentNames.split(",");
                    for (String split : splits) {
                        if(split.equals(contextIntentName)){
                            return transformRelation;
                        }
                    }
                }
            }
        }
        return null;
    }
}
