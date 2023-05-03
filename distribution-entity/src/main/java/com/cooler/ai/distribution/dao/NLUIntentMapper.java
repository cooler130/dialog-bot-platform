package com.cooler.ai.distribution.dao;

import com.cooler.ai.distribution.entity.NLUIntent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NLUIntentMapper {
    Integer insert(NLUIntent nluIntent);

    NLUIntent selectByTwoNames(@Param("nluIntentName") String nluIntentName,
                               @Param("nluDomainName") String nluDomainName);

    List<NLUIntent> selectByNluIntentName(@Param("nluIntentName") String nluIntentName);

    List<NLUIntent> selectByIntentId(@Param("intentId") Integer intentId);

    Integer selectMaxId();
}