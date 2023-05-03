package com.cooler.ai.distribution.service;

import com.cooler.ai.distribution.entity.SlotRelation;
import java.util.List;

public interface SlotRelationService {
    /**
     * 根据slotIds查询nluSlotIds
     * @param slotIds
     * @return
     */
    List<SlotRelation> selectBySlotIds(List<Integer> slotIds);
}
