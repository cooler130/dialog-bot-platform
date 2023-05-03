package com.cooler.ai.distribution.dao;

import com.cooler.ai.distribution.entity.Slot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SlotMapper {

    List<Slot> selectByIntentId(@Param("intentId") Integer intentId);
}