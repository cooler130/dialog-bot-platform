package com.cooler.ai.dm.service;

import com.cooler.ai.dm.entity.TransformRelation;

public interface TransformRelationService {

    TransformRelation selectTransformIntent(String domainName, String taskName, String contextState, String intentName, String version);

}
