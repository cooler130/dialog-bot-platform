package com.cooler.ai.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.EntityConstant;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.model.DomainTaskData;
import com.cooler.ai.platform.service.CacheService;
import com.cooler.ai.platform.service.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("contextService")
public class ContextServiceImpl implements ContextService {

    private Logger logger = LoggerFactory.getLogger(ContextServiceImpl.class);

    @Resource
    private CacheService<DialogState> dialogStateCacheService;

    @Resource
    private CacheService<Map<String, BizDataModelState<String>>> bizDataCacheService;

    @Resource
    private CacheService<DomainTaskData> turnNumCacheService;


    @Override
    public void initCusParams(DMRequest dmRequest) {
        logger.debug("1.1.2-----------------得到的请求数据 dmRequest: ----> " + JSON.toJSONString(dmRequest));
    }

    @Override
    public List<DialogState> getContext(DMRequest dmRequest) {
        logger.debug("1.2.-----------------ContextServiceImpl.getContext(dmRequest) 创建/恢复多轮历史DialogState结构体");

        //1.获取上一轮的domainTaskData，进而构建各个上下文的key集合
        String sessionId = dmRequest.getSessionId();
        DomainTaskData domainTaskData = turnNumCacheService.getContext(sessionId + "_" + Constant.DOMAIN_TASK_DATA);
        if(domainTaskData == null){
            domainTaskData = new DomainTaskData(sessionId, 0, new HashMap());
        }

        //2.获取"领域-任务"单元下的轮次，组合成获取上下文的key，如果是非语言请求类型，则前端埋点已经埋好了领域、任务名称，如果是语言请求类型，则需要组合出多个key
        List<String> historyKeys = new ArrayList<>();
        String queryType = dmRequest.getRequestType();
        if(EntityConstant.NON_LANGUAGE_QUERYTYPES.contains(queryType)){
            Map<String, String> extendInfo = dmRequest.getExtendInfo();
            String domainName = extendInfo.get(PC.DOMAIN_NAME);    //能从这里取出domain和task的名称，需要前端预埋好这两个信息到request的extendInfo中
            String taskName = extendInfo.get(PC.TASK_NAME);
            int taskTurnNum = domainTaskData.getTaskTurnNum(domainName, taskName);
            historyKeys.add(sessionId + "_" + domainName + "::" + taskName + "_" + taskTurnNum + "_" + Constant.DIALOG_STATE);
        }else if(EntityConstant.LANGUAGE_QUERYTYPES.contains(queryType)){
            Map<String, DomainTaskData.DomainData> domainDataMap = domainTaskData.getDomainDataMap();
            for (String domainName : domainDataMap.keySet()) {
                DomainTaskData.DomainData domainData = domainDataMap.get(domainName);
                Map<String, DomainTaskData.TaskData> taskDataMap = domainData.getTaskDataMap();
                for (String taskName : taskDataMap.keySet()) {
                    int taskTurnNum = domainTaskData.getTaskTurnNum(domainName, taskName);
                    historyKeys.add(sessionId + "_" + domainName + "::" + taskName + "_" + taskTurnNum + "_" + Constant.DIALOG_STATE); //获取的是每一个domain_task的最后一轮DS数据
                }
            }

        }

//        dmRequest.setDomainTaskData(domainTaskData);                                                                          //设置当前轮的turnNum

        //3.开始查询
        if(historyKeys.size() > 0){
            try {
                logger.debug("1.2.a.传入cacheService的入参, historyKeys : " + JSON.toJSONString(historyKeys));
                List<DialogState> historyDialogStates = dialogStateCacheService.getContextList(historyKeys);
                List<DialogState> validatedDialogStates = new ArrayList<>();
                if(historyDialogStates != null && historyDialogStates.size() > 0){
                    for (DialogState historyDialogState : historyDialogStates) {
                        if(historyDialogState != null){
                            validatedDialogStates.add(historyDialogState);
                        }
                    }
                }
                Collections.sort(validatedDialogStates, new Comparator<DialogState>() {
                    @Override
                    public int compare(DialogState o1, DialogState o2) {
                        return o1.getTotalTurnNum() > o2.getTotalTurnNum() ? 1 : -1;
                    }
                });
                return validatedDialogStates;

            } catch (Exception e) {
                e.printStackTrace();
                logger.error("1.2.c.SessionService获取DialogState失败！" + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public Map<String, BizDataModelState<String>> getBizData(DMRequest dmRequest) {
        String sessionId = dmRequest.getSessionId();
        try {
            //获取累计业务数据
            Map<String, BizDataModelState<String>> bizDataModelStateMap = bizDataCacheService.getContext(sessionId + "_" + Constant.BIZ_DATA);
            if(bizDataModelStateMap == null) bizDataModelStateMap = new HashMap<>();
            return bizDataModelStateMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }


}
