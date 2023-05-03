package com.cooler.ai.distribution.service;

import com.cooler.ai.distribution.entity.Intent;

public interface IntentService {

    Intent selectByIntentId(Integer intentId);

    Intent selectByTwoNames(String domainName, String intentName);

    void insert(Intent intent);

    Integer selectMaxId();
}
