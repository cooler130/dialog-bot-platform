package com.cooler.ai.platform.service;

import com.cooler.ai.platform.entity.Intent;

public interface IntentService {

    Intent selectByIntentId(Integer intentId);

    Intent selectByTwoNames(String domainName, String intentName);
}
