package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.dao.SlotRelationMapper;
import com.cooler.ai.platform.entity.SlotRelation;
import com.cooler.ai.platform.service.SlotRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("slotRelationService")
public class SlotRelationServiceImpl implements SlotRelationService {

    @Autowired
    private SlotRelationMapper slotRelationMapper;

    @Override
    public List<SlotRelation> selectBySlotIds(List<Integer> slotIds) {
        return slotRelationMapper.selectBySlotIds(slotIds);
    }
}
