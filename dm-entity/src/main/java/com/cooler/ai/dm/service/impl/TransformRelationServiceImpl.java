package com.cooler.ai.dm.service.impl;

import com.cooler.ai.dm.service.TransformRelationService;
import com.cooler.ai.dm.dao.TransformRelationMapper;
import com.cooler.ai.dm.entity.TransformRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("transformRelationService")
public class TransformRelationServiceImpl implements TransformRelationService {

    @Autowired
    private TransformRelationMapper transformRelationMapper;

    @Override
    public TransformRelation selectTransformIntent(String domainName, String taskName, String contextState, String currentIntentName, String version) {
        List<TransformRelation> transformRelations = transformRelationMapper.selectByDTS(domainName, taskName, contextState, version);
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
