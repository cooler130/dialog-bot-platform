package com.cooler.ai.platform.dao;

import com.cooler.ai.platform.entity.*;

import java.util.List;

public interface AllMapper {

    default List<Intent> selectAllIntent() {
        return null;
    }

    List<NLUIntent> selectAllNLUIntent();

    List<NLUSlot> selectAllNLUSlot();

    List<Slot> selectAllSlot();

    List<SlotRelation> selectAllSlotRelation();

}