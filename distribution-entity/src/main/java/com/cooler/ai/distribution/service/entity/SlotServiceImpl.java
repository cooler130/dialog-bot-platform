package com.cooler.ai.distribution.service.entity;

import com.cooler.ai.distribution.service.SlotService;
import com.cooler.ai.distribution.dao.SlotMapper;
import com.cooler.ai.distribution.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("slotService")
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotMapper slotMapper;

    @Override
    public List<Slot> selectByIntentId(Integer intentId) {
        return slotMapper.selectByIntentId(intentId);
    }
}
