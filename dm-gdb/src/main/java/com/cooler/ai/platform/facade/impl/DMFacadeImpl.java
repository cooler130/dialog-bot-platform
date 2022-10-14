package com.cooler.ai.platform.facade.impl;

import com.cooler.ai.platform.entity.Action;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.service.framework.*;
import com.cooler.ai.platform.facade.DMFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component("dmFacade")
public class DMFacadeImpl implements DMFacade {

    @Qualifier("gdbDstService")
    @Autowired
    private DSTService dstService;                                                                                     //3.有限状态机服务

    @Qualifier("policyProcessService")
    @Autowired
    private PolicyProcessService policyProcessService;                                                                 //4.动作选择、执行服务（两个版本：db版和json版）

    @Autowired
    private DataStoreService dataStoreService;                                                                          //5.数据保存服务

    @Override
    public DMResponse process(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
        long startTimeStamp = System.currentTimeMillis();

        dstService.fsmDSTProcess(dmRequest, dialogState, bizDataMSMap);
        Action startAction = policyProcessService.queryPolicy(dialogState);
        DMResponse dmResponse = policyProcessService.runActions(dmRequest, dialogState, startAction, bizDataMSMap);
        dataStoreService.storeData(dmRequest, dialogState, bizDataMSMap);

        long endTimeStamp = System.currentTimeMillis();

        return dmResponse;
    }

}
