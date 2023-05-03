package com.cooler.ai.distribution.dao;

import com.cooler.ai.distribution.entity.Intent;
import org.apache.ibatis.annotations.Param;

public interface IntentMapper {

    Integer insert(Intent intent);

    Intent selectByIntentId(@Param("intentId") Integer intentId);

    Intent selectByTwoNames(@Param("domainName") String domainName, @Param("intentName")String intentName);

    Integer selectMaxId();

}