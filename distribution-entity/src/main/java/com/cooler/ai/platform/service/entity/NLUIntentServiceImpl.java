package com.cooler.ai.platform.service.entity;

import com.cooler.ai.platform.dao.NLUIntentMapper;
import com.cooler.ai.platform.entity.NLUIntent;
import com.cooler.ai.platform.service.NLUIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("nluIntentService")
public class NLUIntentServiceImpl implements NLUIntentService {

    @Autowired
    private NLUIntentMapper nluIntentMapper;

    @Override
    public NLUIntent selectByTwoNames(String nluIntentName, String nluDomainName) {
        return nluIntentMapper.selectByTwoNames(nluIntentName, nluDomainName);
    }

    @Override
    public List<NLUIntent> selectByNluIntentName(String nluIntentName) {
        return nluIntentMapper.selectByNluIntentName(nluIntentName);
    }

    public List<NLUIntent> selectByIntentId(Integer intentId) {
        return nluIntentMapper.selectByIntentId(intentId);
    }

}
