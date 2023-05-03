package com.cooler.ai.dm.facade.impl;

import com.cooler.ai.distribution.facade.DMFacade;
import com.cooler.ai.distribution.facade.model.BizDataModelState;
import com.cooler.ai.distribution.facade.model.DMRequest;
import com.cooler.ai.distribution.facade.model.DMResponse;
import com.cooler.ai.distribution.facade.model.DialogState;
import com.cooler.ai.dm.service.framework.DSTService;
import com.cooler.ai.dm.service.framework.DataStoreService;
import com.cooler.ai.dm.entity.PolicyAction;
import com.cooler.ai.dm.service.framework.PolicyProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("dmFacade")
public class DMFacadeImpl implements DMFacade {

    @Qualifier("gdbDstService")
    @Autowired
    private DSTService dstService;

    @Qualifier("policyProcessService")
    @Autowired
    private PolicyProcessService policyProcessService;

    @Autowired
    private DataStoreService dataStoreService;

    @Override
    public DMResponse process(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
        long startTimeStamp = System.currentTimeMillis();

        dstService.fsmDSTProcess(dialogState);
        List<PolicyAction> actions = policyProcessService.queryPolicy(dialogState);
        DMResponse dmResponse = policyProcessService.runActions(dialogState, actions);
        dataStoreService.storeData(dmRequest, dialogState, bizDataMSMap);

        long endTimeStamp = System.currentTimeMillis();

        return dmResponse;
    }

}
