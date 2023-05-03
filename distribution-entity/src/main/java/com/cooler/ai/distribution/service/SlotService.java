package com.cooler.ai.distribution.service;

import com.cooler.ai.distribution.entity.Slot;
import java.util.List;

public interface SlotService {

    List<Slot> selectByIntentId(Integer intentId);

}
