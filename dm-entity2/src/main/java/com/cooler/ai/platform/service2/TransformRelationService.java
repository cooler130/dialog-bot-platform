package com.cooler.ai.platform.service2;

import com.cooler.ai.platform.entity2.TransformRelation;

public interface TransformRelationService {

    TransformRelation selectTransformIntent(String domainName, String taskName, String contextState, String intentName);

}
