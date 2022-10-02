package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.entity.TransformRelation;

public interface TransformRelationService {

    TransformRelation selectByContextStateIdIntent(Integer contextStateId, String contextIntentName);

}
