package com.cooler.ai.distribution.service.entity;

import com.cooler.ai.distribution.service.SlotRelationService;
import com.cooler.ai.distribution.dao.SlotRelationMapper;
import com.cooler.ai.distribution.entity.SlotRelation;
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
