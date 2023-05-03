package com.cooler.ai.distribution.service.entity;

import com.cooler.ai.distribution.service.NLUSlotService;
import com.cooler.ai.distribution.dao.NLUSlotMapper;
import com.cooler.ai.distribution.entity.NLUSlot;
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
