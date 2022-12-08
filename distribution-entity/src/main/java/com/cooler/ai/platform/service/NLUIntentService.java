package com.cooler.ai.platform.service;

import com.cooler.ai.platform.entity.NLUIntent;
import java.util.List;

public interface NLUIntentService {

    /**
     * 根据连接层意图名称和领域名称查询NLUIntent，获取intent_id
     * @param nluIntentName 连接层意图名
     * @param nluDomainName 连接层领域名
     * @return  DM内部intent_id
     */
    NLUIntent selectByTwoNames(String nluIntentName, String nluDomainName);

    /**
     * 根据nluIntentName查询对应的NLUIntent集合
     * @param nluIntentName
     * @return
     */
    List<NLUIntent> selectByNluIntentName(String nluIntentName);

    /**
     * 根据intentId查询对应的NLUIntent集合
     * @param intentId
     * @return
     */
    List<NLUIntent> selectByIntentId(Integer intentId);
}
