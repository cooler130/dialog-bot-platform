package com.cooler.ai.distribution.facade.impl;

import com.cooler.ai.distribution.facade.DMFacade;
import com.cooler.ai.distribution.facade.model.BizDataModelState;
import com.cooler.ai.distribution.facade.model.DMRequest;
import com.cooler.ai.distribution.facade.model.DMResponse;
import com.cooler.ai.distribution.facade.model.DialogState;
import com.cooler.ai.distribution.facade.DistributionCenterFacade;
import com.cooler.ai.distribution.service.ContextService;
import com.cooler.ai.distribution.service.DMFacadeMapService;
import com.cooler.ai.distribution.service.SlotOperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("distributionCenterFacade")
public class DistributionCenterFacadeImpl implements DistributionCenterFacade {

    @Autowired
    private ContextService contextService;                                                                              //1.对话上下文服务

    //下面是正式环境所使用的组件
    @Qualifier("slotOperateService")
    @Autowired
    private SlotOperateService slotOperateService;                                                                     //2.对话状态追踪服务（两个版本：db版）

    @Qualifier("xmlDMFacadeMapService")
    @Autowired
    private DMFacadeMapService dmFacadeMapService;                                                                      //3.facade映射服务（当前在xml注册各个服务，以后还可以设计其他版本，例如将映射关系放到数据库中）

    public DMResponse distributeProcess(DMRequest dmRequest) {
        long startTimeStamp = System.currentTimeMillis();
        contextService.initCusParams(dmRequest);                                                                        //0.参数初始化（定制化参数初始化（从DB中加载））

        List<DialogState> historyDialogStates = contextService.getContext(dmRequest);                                   //1.读取多轮历史保存对话状态数据（创建/恢复DM结构体）
        Map<String, BizDataModelState<String>> bizDataMSMap = contextService.getBizData(dmRequest);                     //（全量业务数据集）此bizDataMap用来装载动作执行过程中产生的业务数据，此数据形式多变，无法标准化，也无法确定每一轮能产生多少

        DialogState dialogState = slotOperateService.operationSlots(dmRequest, historyDialogStates, bizDataMSMap);

        DMFacade dmFacade = dmFacadeMapService.getFacadeName(dialogState.getBotName());

        DMResponse dmResponse = dmFacade.process(dmRequest, dialogState, bizDataMSMap);

        long endTimeStamp = System.currentTimeMillis();

        dmResponse.setReceiveTime(startTimeStamp);
        dmResponse.setReturnTime(endTimeStamp);

        return dmResponse;
    }
}