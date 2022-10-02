package com.cooler.ai.platform.service.impl.entity;

import com.cooler.ai.platform.dao.TransformRelationMapper;
import com.cooler.ai.platform.entity.TransformRelation;
import com.cooler.ai.platform.service.entity.TransformRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("transformRelationService")
public class TransformRelationServiceImpl implements TransformRelationService {

    @Autowired
    private TransformRelationMapper transformRelationMapper;

    @Override
    public TransformRelation selectByContextStateIdIntent(Integer contextStateId, String contextIntentName) {
        List<TransformRelation> transformRelations = transformRelationMapper.selectByContextStateId(contextStateId);
        if(transformRelations != null && transformRelations.size() > 0){
            for (TransformRelation transformRelation : transformRelations) {
                String contextIntentNames = transformRelation.getContextIntentNames();
                String[] intentNames = contextIntentNames.split(",");
                for (String intentName : intentNames) {
                    if(intentName.equals(contextIntentName)){
                        return transformRelation;
                    }
                }
            }
        }
        return null;
    }
}
