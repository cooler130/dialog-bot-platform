package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.dao.SlotMapper;
import com.cooler.ai.platform.entity.Slot;
import com.cooler.ai.platform.service.SlotService;
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
