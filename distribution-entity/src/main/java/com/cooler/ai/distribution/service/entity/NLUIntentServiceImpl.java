package com.cooler.ai.distribution.service.entity;

import com.cooler.ai.distribution.dao.NLUIntentMapper;
import com.cooler.ai.distribution.entity.NLUIntent;
import com.cooler.ai.distribution.service.NLUIntentService;
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

    @Override
    public void insert(NLUIntent nluIntent) {
        nluIntentMapper.insert(nluIntent);

    }

    @Override
    public Integer selectMaxId() {
        return nluIntentMapper.selectMaxId();
    }

}
