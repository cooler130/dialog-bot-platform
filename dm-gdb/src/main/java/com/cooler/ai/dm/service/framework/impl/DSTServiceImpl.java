package com.cooler.ai.dm.service.framework.impl;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.distribution.facade.constance.Constant;
import com.cooler.ai.distribution.facade.constance.PC;
import com.cooler.ai.distribution.facade.model.DialogState;
import com.cooler.ai.dm.entity.TransformRelation;
import com.cooler.ai.dm.model.ConditionNodeRecord;
import com.cooler.ai.dm.model.ConditionPathRecord;
import com.cooler.ai.dm.model.IntentSetNode;
import com.cooler.ai.dm.service.external.GDBService;
import com.cooler.ai.dm.service.framework.DSTService;
import com.cooler.ai.dm.service.TransformRelationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("gdbDstService")
public class DSTServiceImpl implements DSTService {

    @Qualifier("transformRelationService")
    @Autowired
    private TransformRelationService transformRelationService;

    @Resource
    private GDBService gdbService;

    private Logger logger = LoggerFactory.getLogger(DSTServiceImpl.class);

    @Override
    public void fsmDSTProcess(DialogState dialogState) {
        String domainName = dialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
        String taskName = dialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
        String version = dialogState.getParamValue(PC.VERSION, Constant.PLATFORM_PARAM);
        String intentName = dialogState.getParamValue(PC.INTENT_NAME, Constant.PLATFORM_PARAM);
        String currentState = dialogState.getParamValue(PC.FROM_STATE, Constant.PLATFORM_PARAM);
        if (currentState.equals(Constant.GLOBAL_ERROR) || currentState.equals(Constant.GLOBAL_END)) {
            currentState = Constant.GLOBAL_START;
        }
        logger.info("START            : [ OUT ] : {} ", dialogState.getInteractiveData());
        logger.info("DST Running      : [ D: {}-({}轮), T: {}-({}轮) ] --- [ {} ] + ( {} ) ... ", domainName, dialogState.getDomainTurnNum(), taskName, dialogState.getDomainTaskTurnNum(),  currentState, intentName);

        String fromState2 = currentState;
        //1.找出当前领域、任务下，和currentState状态关联的所有包含intentName的intentSet节点
        List<IntentSetNode> selectedIntentSetNodes = gdbService.getIntentSetsFromState(domainName, taskName, currentState, intentName);

        //2.如果找不到任何intentSet，则尝试将intentName转义为标准意图，再尝试采查询。
        String transformIntentName = intentName;
        if(selectedIntentSetNodes.size() == 0){
            String transformIntentNameTmp = transformStandardIntent(domainName, taskName, currentState, intentName, version);                  //尝试将当前意图转义成标准意图，再进行查询
            if(transformIntentNameTmp != null){
                transformIntentName = transformIntentNameTmp;
                selectedIntentSetNodes = gdbService.getIntentSetsFromState(domainName, taskName, currentState, transformIntentName);
            }
        }

        //3.如果找不到任何intentSet，则currentState换成start节点尝试。
        if(selectedIntentSetNodes.size() == 0 && !currentState.equals(Constant.GLOBAL_START)){
            fromState2 = Constant.GLOBAL_START;
            selectedIntentSetNodes = gdbService.getIntentSetsFromState(domainName, taskName, Constant.GLOBAL_START, intentName);
            if(selectedIntentSetNodes.size() == 0 && !intentName.equals(transformIntentName)){
                selectedIntentSetNodes = gdbService.getIntentSetsFromState(domainName, taskName, Constant.GLOBAL_START, transformIntentName);
            }
        }

        //4.遍历各个意图集节点，尝试找到第一个有效条件路径
        ConditionPathRecord passedConditionPathRecord = null;
        String endStateName = null;
        StringBuffer passedConditionNames = new StringBuffer();
        if(selectedIntentSetNodes.size() == 0){
            //4.1. 没有找到IntentSet节点，则打印日志，看看当前状态没有当前意图是否合理。（最好保存到一个表中）
            endStateName = Constant.GLOBAL_ERROR;
            logger.error("DST 发生错误1     : [ {}-({}轮), {}-({}轮) ] --- [ {} ]  ->  ( {Ø} IntentSet:{} ) ，没有找到从 {} 出发的 {} 相关意图集合节点！请管理员检查是否补充意图能力！",
                    domainName, dialogState.getDomainTurnNum(), taskName, dialogState.getDomainTaskTurnNum(),  currentState, intentName, currentState, intentName);

        }else{
            //4.2.（遍历）收集并检测每一个IntentSet节点，找到第一个"可行"路径
            for (IntentSetNode intentSetNode : selectedIntentSetNodes) {
                passedConditionPathRecord = gdbService.tryConditionPathsFromIntent(intentSetNode, dialogState, currentState, intentName);
                if(passedConditionPathRecord != null) break;
            }

            //4.3.获取终止状态
            if(passedConditionPathRecord != null){
                endStateName = passedConditionPathRecord.getEndStateName();                                                 //如果有有效路径，则endState为路径的尾节点，如果没有，则为GLOBAL_ERROR
                List<ConditionNodeRecord> conditionNodeRecords = passedConditionPathRecord.getConditionNodeRecords();
                if(conditionNodeRecords != null && conditionNodeRecords.size() > 0){
                    dialogState.addToParamValueMap(PC.CHECKED_CONDITIONS, JSON.toJSONString(conditionNodeRecords), Constant.PLATFORM_PARAM);
                    for (ConditionNodeRecord conditionNodeRecord : conditionNodeRecords) {
                        String conditionName = conditionNodeRecord.getConditionName();
                        String conditionWhether = conditionNodeRecord.getConditionWhether();
                        passedConditionNames.append(conditionName).append(conditionWhether.equals("") ? "" : "-").append(conditionWhether).append(" ");
                    }
                }

            } else {
                endStateName = Constant.GLOBAL_ERROR;
                logger.error("DST 发生错误2     : [ {}-({}轮), {}-({}轮) ] --- [ {} ] + ( {} ) -> [ State:{Ø} ] ，没有找到从 {} + {} 出发的相关路径！请管理员检查是否修复交互图！",
                        domainName, dialogState.getDomainTurnNum(), taskName, dialogState.getDomainTaskTurnNum(),  currentState, intentName, currentState, intentName);
            }
        }
        logger.info("------------------");
        logger.info("DST Result       : [ {} {} ] +  ( {} {} ) + { {} } -> [ {} ]。 ",
                currentState,
                fromState2.equals(currentState) ? "" : " s2:"+ fromState2,
                intentName,
                intentName.equals(transformIntentName) ? "" : " t:" + transformIntentName,
                passedConditionNames.toString(),
                endStateName);

        //5.存储状态
        dialogState.addToParamValueMap(PC.FROM_STATE, currentState, Constant.PLATFORM_PARAM);
        dialogState.addToParamValueMap(PC.FROM_STATE2, fromState2, Constant.PLATFORM_PARAM);
        dialogState.addToParamValueMap(PC.TRANSFORM_INTENT_NAME, transformIntentName, Constant.PLATFORM_PARAM);
        dialogState.addToParamValueMap(PC.TO_STATE, endStateName, Constant.PLATFORM_PARAM);
    }

    /**
     * 将一个意图转换为当时语境下的标准意图
     * @param domainName
     * @param taskName
     * @param currentState
     * @param intentName
     * @return
     */
    private String transformStandardIntent(String domainName, String taskName, String currentState, String intentName, String version) {
        TransformRelation transformRelation = transformRelationService.selectTransformIntent(domainName, taskName, currentState, intentName, version);
        if(transformRelation != null){
            String transformIntentName = transformRelation.getTransformIntentName();
            return transformIntentName;
        }
        return null;
    }

}
