package com.cooler.ai.platform.service;

import com.cooler.ai.platform.entity.Slot;
import java.util.List;

public interface SlotService {

    List<Slot> selectByIntentId(Integer intentId);

}
