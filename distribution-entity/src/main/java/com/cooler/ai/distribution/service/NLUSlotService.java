package com.cooler.ai.distribution.service;

import com.cooler.ai.distribution.entity.NLUSlot;

import java.util.List;
import java.util.Set;

public interface NLUSlotService {

    /**
     * 根据slotIds查询相关的NLUSlot集合
     * @param ids   当前intentId的所有slotId集合
     * @return  对应的nluSlot集合
     */
    List<NLUSlot> selectByIds(Set<Integer> ids);
}
