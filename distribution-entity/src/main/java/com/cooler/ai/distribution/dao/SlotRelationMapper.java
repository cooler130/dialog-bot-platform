package com.cooler.ai.distribution.dao;

import com.cooler.ai.distribution.entity.SlotRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SlotRelationMapper {
    /**
     * 根据slotIds集合查询nluSlotIds
     * @param slotIds
     * @return  nluSlotIds
     */
    List<SlotRelation> selectBySlotIds(@Param("slotIds") List<Integer> slotIds);
}