package com.cooler.ai.platform.service.entity_json;

import com.alibaba.fastjson.JSONObject;
import com.cooler.ai.platform.entity.NLUSlot;
import com.cooler.ai.platform.service.NLUSlotService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("jsonNLUSlotService")
public class NLUSlotServiceImpl implements NLUSlotService {

    private static Map<String, String> globalMap = EntityConstant.globalMap;

    @Override
    public List<NLUSlot> selectByIds(Set<Integer> ids) {
        List<NLUSlot> nluSlots = new ArrayList<>();
        for (Integer id : ids) {
            String json = globalMap.get("NLUSlot_" + id);
            if(json != null){
                NLUSlot nluSlot = JSONObject.parseObject(json, NLUSlot.class);
                nluSlots.add(nluSlot);
            }
        }
        return nluSlots;
    }
}
