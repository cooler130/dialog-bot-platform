package com.cooler.ai.platform.service.framework.impl;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.*;
import com.cooler.ai.platform.service.external.CacheService;
import com.cooler.ai.platform.service.framework.DataStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("dataStoreService")
public class DataStoreServiceImpl implements DataStoreService {

    private Logger logger = LoggerFactory.getLogger(DataStoreServiceImpl.class);

    @Resource
    private CacheService<DialogState> cacheService;

    @Resource
    private CacheService<Map<String, ModelState<String>>> cacheServiceBD;

    @Override
    public void storeData(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap) {
        dataCache(dmRequest, dialogState, bizDataMap);
        dataLog(dmRequest, dialogState, bizDataMap);
    }

    private void dataCache(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap) {
        logger.debug("4.1.---------------------------保存DM序列数据（level1）");

        String sessionId = dialogState.getSessionId();
        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
        Integer domainTaskTurnNum = dialogState.getDomainTaskTurnNum();

        //1.保存本轮产生的 DS
        String dialogStateKey = sessionId + "_" + domainName + "::" + taskName + "_" + domainTaskTurnNum + "_" + Constant.DIALOG_STATE;
        cacheService.setContext(dialogStateKey, dialogState);                                                           //dialogState数据（包含本轮整合后的槽位KV数据）

        //2.保存本轮产生的 BIZ_DATA
        Map<String, ModelState<String>> bizDataModelStateMap = new HashMap<>();
        for (String bizKey : bizDataMap.keySet()) {
            BizDataModelState<String> bizDataMS = bizDataMap.get(bizKey);
            String bizDomainName = bizDataMS.getDomainName();
            String bizTaskId = bizDataMS.getTaskName();

            int bizKeepDomainTaskTurnCount = bizDataMS.getKeepDomainTaskTurnCount();
            if(bizKeepDomainTaskTurnCount > 0){
                if(domainName.equals(bizDomainName) && taskName.equals(bizTaskId)){                           //由于业务数据是针对各个话题{domainName+taskId}共享的，所以这里要做一下鉴别，如果当前业务数据跟当前对话是一个话题，才会进行减少使用轮次，不一样则不减少
                    bizDataMS.setKeepDomainTaskTurnCount(bizKeepDomainTaskTurnCount - 1);                      //每保存一轮，就减1，减到0，就自动不保存
                }
                bizDataModelStateMap.put(bizKey, bizDataMS);
            }
        }
//        System.out.println("\n---》可用与下一轮的各个业务数据 BIZ_DATA：" + JSON.toJSONString(bizDataModelStateMap));
        String bizDataKey = sessionId + "_" + Constant.BIZ_DATA;       //业务数据（在各个Action执行后累积起来的业务数据，注意这个key里面没有domainName和taskId，也就是说每轮产生的业务数据跟领域和任务无关，就是说各类话题共享对话过程中产生的业务数据）
        cacheServiceBD.setContext(bizDataKey, bizDataModelStateMap);
    }


    private void dataLog(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMap) {
        String dmRequestJS = JSON.toJSONString(dmRequest);
        String dialogStateJS = JSON.toJSONString(dialogState);
        String bizDataMapJS = JSON.toJSONString(bizDataMap);

//        System.out.println("4.2.1.对话状态数据（DMRequest）：" + dmRequestJS);
//        System.out.println("4.2.2.对话状态数据（DialogState）：" + dialogStateJS);
//        System.out.println("4.2.3.业务数据（bizDataMap）" + bizDataMapJS);

        logger.debug("4.2.1.对话状态数据（DMRequest）：" + dmRequestJS);
        logger.debug("4.2.2.对话状态数据（DialogState）：" + dialogStateJS);
        logger.debug("4.2.3.业务数据（bizDataMap）" + bizDataMapJS);                                     //所有业务数据都放到log中（和上面的保存不一样）

    }


}
