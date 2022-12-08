package com.cooler.ai.platform.service2.impl;

import com.cooler.ai.platform.dao2.TransformRelationMapper;
import com.cooler.ai.platform.entity2.TransformRelation;
import com.cooler.ai.platform.service2.TransformRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("transformRelationService")
public class TransformRelationServiceImpl implements TransformRelationService {

    @Autowired
    private TransformRelationMapper transformRelationMapper;

    @Override
    public TransformRelation selectTransformIntent(String domainName, String taskName, String contextState, String currentIntentName) {
        List<TransformRelation> transformRelations = transformRelationMapper.selectByDTS(domainName, taskName, contextState);
        if(transformRelations != null && transformRelations.size() > 0){
            for (TransformRelation transformRelation : transformRelations) {
                String intentNamesTmp = transformRelation.getIntentNames();
                String[] intentNames = intentNamesTmp.split(",");
                for (String intentName : intentNames) {
                    if(intentName.equals(currentIntentName)){
                        return transformRelation;
                    }
                }
            }
        }
        return null;
    }
}
