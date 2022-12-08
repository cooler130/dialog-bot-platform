package com.cooler.ai.platform.facade.impl;

import com.cooler.ai.platform.entity2.PolicyAction;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.service.framework.*;
import com.cooler.ai.platform.facade.DMFacade;
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
