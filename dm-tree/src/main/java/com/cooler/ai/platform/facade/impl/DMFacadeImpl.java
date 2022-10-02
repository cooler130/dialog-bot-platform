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

    @Qualifier("dstJsonService")
    @Autowired
    private DSTService dstService;                                                                                      //3.有限状态机服务

    @Qualifier("policyProcessJsonService")
    @Autowired
    private PolicyProcessService policyProcessService;                                                                  //4.动作选择、执行服务（两个版本：db版和json版）

    @Autowired
    private DataStoreService dataStoreService;                                                                          //5.数据保存服务

    @Override
    public DMResponse process(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {

        long startTimeStamp = System.currentTimeMillis();

        dstService.fsmDSTProcess(dmRequest, dialogState, bizDataMSMap);                                                 //  1.状态检测、迁移过程追踪（DST过程），检测各个槽位的状态，以及迁移过程，得到最终状态
        Action startAction = policyProcessService.queryPolicy(dialogState);                                             //  2.根据策略，查询动作
        DMResponse dmResponse = policyProcessService.runActions(dmRequest, dialogState, startAction, bizDataMSMap);     //  3.执行动作（globalMap会记录中间产生的链式动作集合），这个过程bizDataMSMap会新增各个业务参数，它是一个增量集合
        dataStoreService.storeData(dmRequest, dialogState, bizDataMSMap);                                               //  4.保存数据，下一轮需要的DialogState（同步进行）

        long endTimeStamp = System.currentTimeMillis();

        return dmResponse;
    }

}
