package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.dao.NLUSlotMapper;
import com.cooler.ai.platform.entity.NLUSlot;
import com.cooler.ai.platform.service.NLUSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("nluSlotService")
public class NLUSlotServiceImpl implements NLUSlotService {

    @Autowired
    private NLUSlotMapper nluSlotMapper;


    @Override
    public List<NLUSlot> selectByIds(Set<Integer> ids) {
        return nluSlotMapper.selectByIds(ids);
    }
}
