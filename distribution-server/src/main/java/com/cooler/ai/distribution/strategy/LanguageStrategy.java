package com.cooler.ai.distribution.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cooler.ai.distribution.entity.*;
import com.cooler.ai.distribution.facade.constance.Constant;
import com.cooler.ai.distribution.facade.constance.PC;
import com.cooler.ai.distribution.facade.model.DMRequest;
import com.cooler.ai.distribution.facade.model.DialogState;
import com.cooler.ai.distribution.model.*;
import com.cooler.ai.distribution.service.*;
import com.cooler.ai.nlu.DomainInfo;
import com.cooler.ai.nlu.SlotInfo;
import com.cooler.ai.distribution.EntityConstant;
import com.cooler.ai.distribution.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("languageStrategy")
public class LanguageStrategy {
    private Logger logger = LoggerFactory.getLogger(LanguageStrategy.class);
    private final Byte INHERITABLE = 1;                 //可继承的
    private final Byte INHERIT_DESTRUCTIBLE = 1;        //继承破坏的

    @Value("${nlu.url.withContext}")
    private String nluUrlWithContext;

    @Value("${nlu.url.noContext}")
    private String nluUrlNoContext;

    public DomainDesionData decide(DMRequest dmRequest, List<DialogState> historyDialogStates, DomainTaskData domainTaskData,
                                   NLUIntentService nluIntentService, NLUSlotService nluSlotService, IntentService intentService, SlotService slotService, SlotRelationService slotRelationService,
                                   Map<String, String> domainDecisionMap) {

        List<DomainDesionData> domainDesionDatas = operate(dmRequest, historyDialogStates, domainTaskData.getTotalTurnNum(), Constant.MAX_BACKUP_TURN_COUNT,
                nluIntentService, nluSlotService, intentService, slotService, slotRelationService, domainDecisionMap);
        //todo:在这一部分任重道远，需要做一下领域、意图和槽位的澄清（DomainDesionData类需要加入NLU对领域、意图和槽位的解析分数，依据分数进行澄清，分数阈值需要动态设置到数据库中）
        if (domainDesionDatas != null && domainDesionDatas.size() > 0) {
            DomainDesionData domainDesionData = domainDesionDatas.get(0);                                     //经过对DomainIndicator排序，已经将最佳DomainData放到了第一个
            return domainDesionData;
        } else {
            logger.error("2.1.g-4.系统错误，没有选择出最佳业务结构体。");                                                  //todo:!!!!!!!!!!!!!!!!这里可以加入分支，如果选择出的几个领域和意图的分数没有达到阈值，则进行澄清，如果根本没有选择出领域意图（连备选的都没有）则兜底
        }
        return null;
    }

    /**
     * 通过槽位填充和计算，选择出NLU事件渠道合适的业务数据（m*n版）
     * @param dmRequest                 原始请求
     * @param historyDialogStates       历史DS集合
     * @param totalTurnNum              当前sessionId的总轮次
     * @param acceptDataCount           （接受的数据量）记忆轮次数量
     * @param decisionProcessMap        决策过程数据
     * @param nluIntentService          nluIntent数据服务
     * @param nluSlotService            nluSlot数据服务
     * @param intentService             intent数据服务
     * @param slotService               slot数据服务
     * @param slotRelationService       slotRelation数据服务
     * @return  这里返回的是多个最终结果，而上面operationSlots方法中只用了第0个（最佳的）。
     */
    private List<DomainDesionData> operate(DMRequest dmRequest, List<DialogState> historyDialogStates, int totalTurnNum, int acceptDataCount,
                                           NLUIntentService nluIntentService, NLUSlotService nluSlotService, IntentService intentService, SlotService slotService, SlotRelationService slotRelationService,
                                           Map<String, String> decisionProcessMap) {
        //1.准备好变量值
        String sessionId = dmRequest.getSessionId();
        String currentSlotVersion = sessionId + "_" + totalTurnNum;                                                 //这个作为各个抽出的槽位的版本号
        List<DomainIndicator> domainIndicators = new ArrayList<>();                                                 //领域指标数据（装载领域意图决策的特征数据）
        Map<String, DomainDesionData> domainDesionDatasMap = new HashMap<>();                                       //领域业务数据（装载领域意图内部的业务数据）

        //2.收集历史DialogStates中的domainName集合，用这些领域尝试查询NLU，获得多个领域的意图解析结果
        Set<String> domainNames = new HashSet<>();
        for (DialogState historyDialogState : historyDialogStates) {
            String domainName = historyDialogState.getDomainName();
            domainNames.add(domainName);
        }

        String sentence = dmRequest.getQuery();
        List<DomainInfo> allDomainInfos = new ArrayList<>();                                                        //装载NLU中解析出的多个领域的数据（后续根据此集合来准备下面的领域指标数据和领域业务数据）
        if(domainNames.size() > 0){
            for (String domainName : domainNames){
                try {
                    String httpResponseJS = HttpUtil.doGet4Rest(nluUrlWithContext, Arrays.asList(sentence, domainName), null);
                    //todo：1.如果那边需要NLU接口创建Token（现在是支持的），则将Token赋予Access-Token，放到httpHeader中。
                    //todo：2.NLU的解析，需要带上最近一两个（领域-任务之下的状态，对应状态下需要的意图集合，都是可以查到的），这样让NLU有了"参考"，将解析结果"靠近"所需结果，算是一种"作弊"
                    HttpResponse<JSONArray> httpResponse = JSON.parseObject(httpResponseJS, HttpResponse.class);
                    int code = httpResponse.getCode();
                    if (code == 200) {
                        JSONArray data = httpResponse.getData();
                        List<DomainInfo> domainInfos = JSON.parseArray(data.toJSONString(), DomainInfo.class);
                        allDomainInfos.addAll(domainInfos);
                    } else {
                        String message = httpResponse.getMessage();
                        logger.error("请求 {} 返回码： {} ，入参 sentence ：{}, domainName : {} ,返回信息：{} 。", nluUrlWithContext, code, sentence, domainName, message);
                    }
                } catch (Exception e) {
                    logger.error("请求 {} 失败，入参 sentence ：{}, domainName : {}。", nluUrlWithContext, sentence, domainName, e);
                }
            }
        } else {
            try {
                String httpResponseJS = HttpUtil.doGet4Rest(nluUrlNoContext, Arrays.asList(sentence), null);
                HttpResponse<JSONArray> httpResponse = JSON.parseObject(httpResponseJS, HttpResponse.class);
                int code = httpResponse.getCode();
                if (code == 200) {
                    JSONArray data = httpResponse.getData();
                    List<DomainInfo> domainInfos = JSON.parseArray(data.toJSONString(), DomainInfo.class);
                    allDomainInfos.addAll(domainInfos);
                } else {
                    String message = httpResponse.getMessage();
                    logger.error("请求 {} 返回码： {} ，入参 sentence ：{} ,返回信息：{} 。", nluUrlNoContext, code, sentence, message);
                }
            } catch (Exception e) {
                logger.error("请求 {} 失败，入参 sentence ：{} 。", nluUrlNoContext, sentence, e);
            }
        }


        //3.遍历上面得到的NLU的DomainInfo集合（意图解析结果），找到最佳的一个（两类工作：3.1.明确domainName、taskName、intentName和目标Intent对象； 3.2.建立继承了上下文的槽位集；）
        for (int i = 0; i < allDomainInfos.size(); i++) {                                                           //遍历各个领域对象，进行比较，选择最优领域
            DomainInfo domainInfo = allDomainInfos.get(i);

            double nluDomainScore = domainInfo.getScore();                                                          //NLU解析得到的领域置信度得分
            int valuedNecessarySlotCount = 0;                                                                       //有值的必须槽位个数
            int valuedImportantSlotCount = 0;                                                                       //有值的重要槽位个数
            float totalImportanceDegree = 0f;                                                                       //总重要程度
            int totalValuedSlotCount = 0;                                                                           //有值槽位总体个数

            Map<String, SlotState> historySlotStateMap = null;                                                      //历史槽位记录
            Map<String, SlotState> currentSlotStateMap = null;                                                      //本轮产生的槽位记录
            Map<String, String> exchangedRecordMap = null;                                                          //本轮槽位值替换历史槽位值的记录

            //------------------------------------------开始决策domainName、taskName、intentName和目标Intent对象
            //3.1.a.将NLU槽位值进行聚合并收集
            Map<String, List<SlotInfo>> currentNluSlotInfoListMap = aggregateNLUSlots(domainInfo);                  //本轮产生的聚合的NLU模块的SlotInfo集合，将nlu领域中的槽位按槽位名称进行聚合收集，得到一个Map
            Set<String> currentNluSlotNames = currentNluSlotInfoListMap.keySet();                                   //本轮产生的聚合后的NLU槽位名称集合

            DialogState historyDialogState = null;
            String historyNluDomainName = null;                                                                         //上轮选择的NLU领域
            String historyDomainName = null;                                                                        //上轮选择的DM领域
            if (historyDialogStates != null && historyDialogStates.size() > 0) {                                    //准备好上轮NLU领域和DM领域
                historyDialogState = historyDialogStates.get(0);                                                    //上一轮的dialogState
                historyNluDomainName = historyDialogState.getParamValue(PC.NLU_DOMAIN_NAME, Constant.PLATFORM_PARAM);
                historyDomainName = historyDialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
            }

            //3.1.b.先明确此NLU意图对应DM意图的intentId，决策出意图对应的taskId（此步骤决定了后面所用到的目标意图，nluDomainName，决定了其taskId，进而有利于分发各个bot）
            String nluDomainName = domainInfo.getNluDomainName();
            String nluIntentName = domainInfo.getNluIntentName();
            nluDomainName = nluDomainName != null ? nluDomainName : EntityConstant.NO_DOMAIN;
            nluIntentName = nluIntentName != null ? nluIntentName : EntityConstant.NO_INTENT;

            String domainName = null;                                                                               //待确定的领域名称
            String taskName = null;                                                                                 //待确定的任务名称
            String intentName = null;                                                                               //待确定的意图名称

            boolean isNoIntent = nluIntentName.equals(EntityConstant.NO_INTENT);
            boolean isUnknownIntent = nluIntentName.equals(EntityConstant.UNKNOWN_INTENT);

            if(isNoIntent || isUnknownIntent){                                                                      //NLU模块中意图没解析出来，则不应该信任其解析出来的领域，这里领域设置为NO_DOMAIN，自动设置为NO_DOMAIN
                nluDomainName = EntityConstant.NO_DOMAIN;
            }

            //3.1.c.开始执行下面的策略，为domainName、taskName、intentName 赋值
            if(nluDomainName.equals(EntityConstant.NO_DOMAIN)){
                //策略1：NO_DOMAIN && (NO_INTENT_ID || UNKNOWN_INTENT)，相见《语义分发策略》
                if(isNoIntent || isUnknownIntent){
                    //此处nluDomainName还是为 NO_DOMAIN 不变
                    if(historyDialogState != null) {
                        domainName = historyDialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
                        taskName = historyDialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
                        intentName = nluIntentName;
                    }else{
                        domainName = EntityConstant.GUIDE_DOMAIN;
                        taskName = EntityConstant.NO_TASK;
                        intentName = nluIntentName;
                    }
                }
                //策略2：NO_DOMAIN && (单/多领域 单/多任务的意图)，相见《语义分发策略》
                else {
                    List<NLUIntent> nluIntents = nluIntentService.selectByNluIntentName(nluIntentName);
                    if(nluIntents == null || nluIntents.size() == 0){           //没有查到记录，则将意图设置为unknown_intent，然后使用第2条规则的策略；
                        intentName = EntityConstant.UNKNOWN_INTENT;
                        if(historyDialogState != null) {
                            domainName = historyDialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
                            taskName = historyDialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
                        }else{
                            domainName = EntityConstant.GUIDE_DOMAIN;
                            taskName = EntityConstant.NO_TASK;
                        }
                    }else if(nluIntents.size() == 1){                           //查到一个意图记录，则使用记录中的领域和task_id；
                        NLUIntent nluIntent = nluIntents.get(0);
                        Integer intentId = nluIntent.getIntentId();
                        String[] params = getDomainIntentTaskByIntentId(intentId, intentService, historyDialogStates);
                        domainName = params[0];
                        intentName = params[1];
                        taskName = params[2];
                    }else{                                                      //查到多个意图记录，则使用距离最近轮的领域以及task_id；
                        Map<String, NLUIntent> nluDomainNameIntentMap = new HashMap<>();    //按照nluDomainName将各个NluIntent装载起来
                        for (NLUIntent nluIntent : nluIntents) {
                            String nluDomainNameTmp = nluIntent.getNluDomainName();
                            nluDomainNameIntentMap.put(nluDomainNameTmp, nluIntent);
                        }
                        boolean isFixed = false;
                        Set<String> nluDomainNames = nluDomainNameIntentMap.keySet();
                        if(historyDialogStates != null && historyDialogStates.size() > 0){
                            for (DialogState historyDialogStateTmp : historyDialogStates) {
                                String historyNluDomainNameTmp = historyDialogStateTmp.getParamValue(PC.NLU_DOMAIN_NAME, Constant.PLATFORM_PARAM);
                                boolean containSameDomainToHistory = nluDomainNames.contains(historyNluDomainNameTmp) && !historyNluDomainNameTmp.equals("no_domain");
                                if(!containSameDomainToHistory){
                                    String historyIntentId = historyDialogStateTmp.getParamValue(PC.INTENT_ID, Constant.PLATFORM_PARAM);
                                    List<NLUIntent> nluIntentsTmp = nluIntentService.selectByIntentId(Integer.parseInt(historyIntentId));   //根据历史DS查出NLUIntent
                                    if(nluIntentsTmp != null && nluIntentsTmp.size() > 0){
                                        NLUIntent nluIntent = nluIntentsTmp.get(0);
                                        String selectedHistoryNluDomainName = nluIntent.getNluDomainName();
                                        historyNluDomainNameTmp = selectedHistoryNluDomainName;
                                        containSameDomainToHistory = nluDomainNames.contains(selectedHistoryNluDomainName) && !selectedHistoryNluDomainName.equals("no_domain");
                                    }
                                }

                                if(containSameDomainToHistory){
                                    NLUIntent nluIntent = nluDomainNameIntentMap.get(historyNluDomainNameTmp);
                                    String[] params = getDomainIntentTaskByIntentId(nluIntent.getIntentId(), intentService, Arrays.asList(historyDialogStateTmp));
                                    domainName = params[0];
                                    intentName = params[1];
                                    taskName = params[2];
                                    isFixed = true;
                                    break;
                                }
                            }
                        }
                        if(!isFixed){                                           //既然没有上下文或者上下文的nluDomain都不包含在当前查到的nluDomain集合里面，则默认用第一个查到的NLUIntent（默认此分值最高）
                            Integer intentId = nluIntents.get(0).getIntentId();
                            String[] params = getDomainIntentTaskByIntentId(intentId, intentService, historyDialogStates);
                            domainName = params[0];
                            intentName = params[1];
                            taskName = params[2];
                            isFixed = true;
                        }
                    }
                }
            }
            //策略3：非NO_DOMAIN && (单领域 且 单/多任务 的意图)，详见《语义分发策略》
            else {
                NLUIntent nluIntent = nluIntentService.selectByTwoNames(nluIntentName, nluDomainName);              //根据NLU模块传来的这两个字段，确定DM中对应的intent_id
                logger.debug("2.1.b-1.NLU提供了意图，得到的nluIntent为：" + JSON.toJSONString(nluIntent));
                if (nluIntent == null) {
                    logger.error("2.1.b-2.系统错误，nluIntentName : " + nluIntentName + ", nluDomainName: " + nluDomainName + " ，没有查询到nlu_intent表中的记录！这里不能取上轮DS中的意图，因为本轮意图明确，但DM数据错误没能处理，故兜底！");
                    Intent intent = intentService.selectByTwoNames(nluDomainName, EntityConstant.UNKNOWN_INTENT);
                    if(intent == null){
                        domainName = EntityConstant.GUIDE_DOMAIN;
                        intentName = EntityConstant.UNKNOWN_INTENT;
                        taskName = EntityConstant.NO_TASK;
                    }else{
                        Integer intentId = intent.getId();
                        String[] params = getDomainIntentTaskByIntentId(intentId, intentService, historyDialogStates);
                        domainName = params[0];
                        intentName = params[1];
                        taskName = params[2];
                    }
                } else {
                    Integer intentId = nluIntent.getIntentId();                                                             //这是DM系统的IntentID，用来查询DM内部Intent
                    String[] params = getDomainIntentTaskByIntentId(intentId, intentService, historyDialogStates);
                    domainName = params[0];
                    intentName = params[1];
                    taskName = params[2];
                }
            }

            //3.1.d.根据赋值的domainName、taskName、intentName，找到对应的目标Intent对象
            Integer intentId = null;
            Intent targetIntent = intentService.selectByTwoNames(domainName, intentName);
            if(targetIntent == null){
                intentId = EntityConstant.NO_INTENT_ID;
                targetIntent = intentService.selectByIntentId(intentId);
            }else{
                intentId = targetIntent.getId();
            }

            //------------------------------------------开始建立继承了上下文的槽位集；
            //3.2.a.确定此NLU意图对应DM意图的槽位信息
            List<Slot> slots = slotService.selectByIntentId(intentId);                                              //此DM的Intent下含有的slot集合
            Map<Integer, SlotState> currentIntentSlotStateMap = new HashMap<>();                                    //将本轮得到的槽位信息包装体SlotState集合收集起来
            List<Integer> slotIds = new ArrayList<>();                                                              //将DM意图的槽位ID集合收集起来
            currentSlotStateMap = new HashMap<>();
            if (slots != null) {
                for (Slot slot : slots) {
                    SlotState slotState = new SlotState();
                    slotState.setSlotId(slot.getId());
                    slotState.setSlotName(slot.getSlotName());
                    slotState.setIsNecessary(slot.getIsNecessary());
                    slotState.setImportanceDegree(slot.getImportanceDegree());
                    slotState.setInheritable(slot.getInheritable());
                    slotState.setInheritDestructible(slot.getInheritDestructible());
                    slotState.setSlotMsg(slot.getMsg());
                    slotState.setDefaultQuery(slot.getDefaultQuery());                                              //如果这个slotState的is_necessary=0，则此属性为'none'
                    slotState.setVersion(currentSlotVersion);                                                       //此为每一个slotState的唯一版本号
                    currentIntentSlotStateMap.put(slot.getId(), slotState);                                         //slotMap记录DM和nlu里面的槽位对应值，但在此时，值还没有记录进去
                    slotIds.add(slot.getId());
                }
            }

            //3.2.b.将获取的nluSlot集合转变为DM内部的槽位对象，并将NLU解析的槽位值填入此槽位对象中
            if (slotIds.size() > 0) {
                List<SlotRelation> slotRelations = slotRelationService.selectBySlotIds(slotIds);
                Map<Integer, List<Integer>> nluSlotSlotIdMap = new HashMap<>();                                     //构建 Map<nluSlotId, List<slotId>>，留以后用
                if (slotRelations != null && slotRelations.size() > 0) {
                    for (SlotRelation slotRelation : slotRelations) {
                        Integer nluSlotId = slotRelation.getNluSlotId();
                        Integer slotId = slotRelation.getSlotId();
                        List<Integer> slotIdsGroup = nluSlotSlotIdMap.get(nluSlotId);
                        if (slotIdsGroup == null) {
                            slotIdsGroup = new ArrayList<>();
                        }
                        slotIdsGroup.add(slotId);
                        nluSlotSlotIdMap.put(nluSlotId, slotIdsGroup);
                    }
                }

                Set<Integer> nluSlotIds = nluSlotSlotIdMap.keySet();
                if (nluSlotIds != null && nluSlotIds.size() > 0) {
                    List<NLUSlot> nluSlots = nluSlotService.selectByIds(nluSlotIds);
                    for (NLUSlot nluSlot : nluSlots) {
                        String nluSlotName = nluSlot.getNluSlotName();
                        List<SlotInfo> nluSlotInfosTmp = currentNluSlotInfoListMap.get(nluSlotName);                //按名称抽取本轮NLUSlot集

                        Integer nluSlotId = nluSlot.getId();
                        List<Integer> slotIdsGroup = nluSlotSlotIdMap.get(nluSlotId);                               //现在一个nluSlotId对应了多个slotIds了，

                        SLOT_ID:
                        for (Integer slotId : slotIdsGroup) {                                                       //那么要一个个尝试哪个slotId是本轮对话有的那个对应于nluSlotId的那个槽位编号
                            SlotState slotState = currentIntentSlotStateMap.get(slotId);                                      //找到这个正确编号下的SlotState
                            if (slotState != null && nluSlotInfosTmp != null) {
                                slotState.setSlotInfos(nluSlotInfosTmp);                                            //此处才真正将本轮NLU产生的slotInfo集合设置到每一个后面使用的slotState中，属于填槽过程
                                currentNluSlotInfoListMap.remove(nluSlotName);                                            //将用上的槽位值删掉（留下没用上的槽位值名，后面进行处理）
                                break SLOT_ID;
                            }
                        }
                    }
                }
            }

            //3.2.c.将NLU解析出但没被DM记录的槽位值日志打印以备人工后续做关联；并放入一个Map中，以备后用。
            Map<String, SlotState> unknownSlotStateMapTmp = null;                                                   //前面NLU解析出来的值可能有些不被DM识别，那么统一装到这个List里面，有待处理。
            if (currentNluSlotNames != null && currentNluSlotNames.size() > 0) {
                logger.warn("2.1.e.警告！此NLU解析的槽位值名称在DM中没有对应上相关槽位。nluDomainName为： " + nluDomainName + "   ，nluIntentName为： " + nluIntentName + "，名称集合为：  " + JSON.toJSONString(currentNluSlotNames));
                //将没有用上的nluSlot收集起来
                unknownSlotStateMapTmp = new HashMap<>();
                for (String nluSlotName : currentNluSlotNames) {
                    List<SlotInfo> unknownSlotInfos = currentNluSlotInfoListMap.get(nluSlotName);
                    for (SlotInfo unknownSlotInfo : unknownSlotInfos) {
                        SlotState slotState = new SlotState();
                        slotState.setSlotId(-1);
                        slotState.setSlotName(unknownSlotInfo.getName());
                        slotState.setNluSlotName(unknownSlotInfo.getName());
                        List<SlotInfo> slotInfos = slotState.getSlotInfos();
                        if (slotInfos == null) {
                            slotInfos = new ArrayList<>();
                        }
                        slotInfos.add(unknownSlotInfo);
                        slotState.setSlotInfos(slotInfos);
                        slotState.setVersion(currentSlotVersion);
                        unknownSlotStateMapTmp.put(unknownSlotInfo.getName(), slotState);
                    }
                }
            }

            //3.1.f.分两种情况（有上下文和无上下文），结合业务情况，将上文中的历史槽位数据和本轮的槽位数据进行整合（这里需要更进一步的梳理和扩展）
            if (historyDialogStates != null && historyDialogStates.size() > 0) {
                for (int j = 0; j < historyDialogStates.size(); j ++) {
                    DialogState historyDialogStateTmp2 = historyDialogStates.get(j);
                    String historySessionId = historyDialogStateTmp2.getSessionId();
                    int historyTotalTurnNum = historyDialogStateTmp2.getTotalTurnNum();
                    boolean sameDomainTmp = false;
                    Map<String, SlotState> fixedSlotStateMap = new HashMap<>();
                    String historyFromState = EntityConstant.GLOBAL_START;
                    String historyToState = EntityConstant.GLOBAL_START;

                    if (historyDialogStateTmp2 != null) {
                        historyFromState = historyDialogStateTmp2.getParamValue(PC.FROM_STATE, Constant.PLATFORM_PARAM);
                        historyToState = historyDialogStateTmp2.getParamValue(PC.TO_STATE, Constant.PLATFORM_PARAM);

                        //2.1.f1-a.完成3件事：    1.当前领域上下文无关时，领域的重新设置    2.判断本轮对话是否该继承上轮对话的槽位     3.判断本轮和上轮对话是否同领域
                        boolean shouldExtendLastSlots = false;                                                      //判断上轮领域和本轮领域是否相同，并且在没切换领域的情况下，判断是否继续继承槽位
                        Set<String> extendableSlots = null;                                                         //当前可以继承的槽位

                        if (historyDomainName != null) {
                            String currentDomainName = targetIntent.getDomainName();
                            if (historyDomainName.equals(currentDomainName)) {                                           //没切换领域
                                shouldExtendLastSlots = true;                                                       //没有切换领域，则标记为默认可以继承
                                sameDomainTmp = true;                                                               //上下轮对话同领域标示
                                if (currentIntentSlotStateMap != null && currentIntentSlotStateMap.size() > 0) {
                                    Collection<SlotState> currentSlotStates = currentIntentSlotStateMap.values();
                                    if (currentSlotStates != null && currentSlotStates.size() > 0) {
                                        extendableSlots = getExtendableSlots(currentSlotStates, targetIntent.getId(), slotService);
                                        shouldExtendLastSlots = extendableSlots != null && extendableSlots.size() > 0;
                                    }
                                }
                            } else {                                                                                //切换领域了
                                shouldExtendLastSlots = false;                                                      //切换领域了，则标记不可继承
                                sameDomainTmp = false;
                            }
                        } else {                                                                                    //上轮没领域，即当前为第一轮对话
                            shouldExtendLastSlots = false;
                            sameDomainTmp = false;
                        }

                        //2.1.f1-b.如果本轮对话能继承上轮对话的槽位，则先将上轮的历史槽位放入一个槽位Map中，后面用本轮对话槽位值去覆盖。
                        if (shouldExtendLastSlots && extendableSlots != null && extendableSlots.size() > 0) {
                            historySlotStateMap = historyDialogStateTmp2.getFromModelStateMap(Constant.SLOT_STATE_MAP, HashMap.class);          //todo:这里可能需要取出历史轮的业务数据，但又一想，业务数据不作为用户自己说过的信息，只有用户说过的信息用户才期望后面说的时候被"被省略"，才应该被继承，这正是每个DS中的SLOT_STATE_MAP，看效果吧
                            if(historySlotStateMap != null){
                                for (String historySlotName : historySlotStateMap.keySet()) {                       //加载历史SlotState集合
                                    if(extendableSlots.contains(historySlotName)){                                  //这里相当于做一道过滤，过滤掉那些本轮意图不需要的槽位，进而在shouldExtendLastSlots = true（可以被继承）的情况下，阻止那些跟本轮意图无关槽位的槽位保留下来。
                                        SlotState historySlotState = historySlotStateMap.get(historySlotName);
                                        byte inheritable = historySlotState.getInheritable();
                                        if(inheritable == 1){                                                       //这个槽位能被继承才会被继承
                                            fixedSlotStateMap.put(historySlotName, historySlotState);
                                        }
                                    }
                                }
                            }
                        }

                        //2.1.f1-c.然后将本轮获取的槽位放到前面的槽位Map中，该覆盖的历史槽位就直接覆盖掉了
                        if (currentIntentSlotStateMap != null && currentIntentSlotStateMap.size() > 0) {
                            exchangedRecordMap = new HashMap<>();                                                   //新旧槽位更换记录
                            for (SlotState slotState : currentIntentSlotStateMap.values()) {                        //加载本轮SlotState集合
                                String slotName = slotState.getSlotName();
                                List<SlotInfo> slotInfos = slotState.getSlotInfos();
                                if (slotInfos != null && slotInfos.size() > 0) {
                                    currentSlotStateMap.put(slotState.getSlotName(), slotState);                    //这个Map是为了记录之用

                                    SlotState slotStateTmp = fixedSlotStateMap.get(slotName);                       //记录槽位替换记录
                                    if (slotStateTmp != null) {
                                        String version = slotStateTmp.getVersion();
                                        if (version != currentSlotVersion) {                                        //如果版本不一致，则说明此slotState是一个历史版本
                                            List<SlotInfo> historySlotInfos = slotStateTmp.getSlotInfos();
                                            exchangedRecordMap.put(slotName, JSON.toJSONString(slotInfos) + " ---> " + JSON.toJSONString(historySlotInfos));
                                        }
                                    }

                                    fixedSlotStateMap.put(slotName, slotState);                                     //TODO:结合现实构思！
                                }
                            }
                        }
                    }

                    //3.1.g.计算各个槽位的指标参数
                    for (SlotState slotState : fixedSlotStateMap.values()) {
                        Float importanceDegree = slotState.getImportanceDegree();
                        Byte isNecessary = slotState.getIsNecessary();
                        valuedNecessarySlotCount += (isNecessary == (byte) 1 ? 1 : 0);
                        valuedImportantSlotCount += (importanceDegree > 0 ? 1 : 0);
                        totalImportanceDegree += importanceDegree;
                        totalValuedSlotCount++;
                    }

                    Map<String, String> globalBizParamMap = historyDialogStateTmp2.getFromModelStateMap(Constant.BIZ_PARAM_MAP, Map.class);

                    //3.1.h.收集上面整理好的各种数据，包装成评判数据体和业务数据体
                    domainIndicators.add(new DomainIndicator("DO:" + i + "_DS:" + j, historyNluDomainName, historyDomainName, nluDomainName, nluIntentName, nluDomainScore, valuedNecessarySlotCount, valuedImportantSlotCount, totalImportanceDegree, totalValuedSlotCount, historyTotalTurnNum));
                    domainDesionDatasMap.put("DO:" + i + "_DS:" + j, new DomainDesionData(historySessionId, historyTotalTurnNum, sentence, nluDomainName, nluIntentName, domainName, intentName, intentId, taskName, historyFromState, historyToState, sameDomainTmp, historySlotStateMap, currentSlotStateMap, exchangedRecordMap, fixedSlotStateMap, unknownSlotStateMapTmp, globalBizParamMap, null));
                }
            }
            else {
                boolean sameDomainTmp = false;
                Map<String, SlotState> fixedSlotStateMap = new HashMap<>();

                if (currentIntentSlotStateMap != null && currentIntentSlotStateMap.size() > 0) {
                    for (SlotState slotState : currentIntentSlotStateMap.values()) {
                        fixedSlotStateMap.put(slotState.getSlotName(), slotState);
                    }
                }

                //3.1.i.计算各个槽位的指标参数
                for (SlotState slotState : fixedSlotStateMap.values()) {
                    Float importanceDegree = slotState.getImportanceDegree();
                    Byte isNecessary = slotState.getIsNecessary();
                    valuedNecessarySlotCount += (isNecessary == (byte) 1 ? 1 : 0);
                    valuedImportantSlotCount += (importanceDegree > 0 ? 1 : 0);
                    totalImportanceDegree += importanceDegree;
                    totalValuedSlotCount++;
                }

                //3.1.j.收集上面整理好的各种数据，包装成评判数据体和业务数据体
                domainIndicators.add(new DomainIndicator("DO:" + i + "_DS:" + 0, historyNluDomainName, historyDomainName, nluDomainName, nluIntentName, nluDomainScore, valuedNecessarySlotCount, valuedImportantSlotCount, totalImportanceDegree, totalValuedSlotCount, totalTurnNum));
                domainDesionDatasMap.put("DO:" + i + "_DS:" + 0, new DomainDesionData(sessionId, totalTurnNum, sentence, nluDomainName, nluIntentName, domainName, intentName, intentId, taskName, EntityConstant.GLOBAL_START, EntityConstant.GLOBAL_START, sameDomainTmp, historySlotStateMap, currentSlotStateMap, exchangedRecordMap, fixedSlotStateMap, unknownSlotStateMapTmp, null, null));
            }
        }

        //3.1.h.根据上面准备好的评判指标数据，进行排序选择最佳领域，并取与其对应的业务数据
        if (domainIndicators.size() > 0 && domainDesionDatasMap.size() > 0) {
            Collections.sort(domainIndicators);
            //保存排序后的NLU解析结果（最后一个领域被选中）
            logger.debug("2.1.g-1.得到domainIndicators，最后一个最佳 : " + JSON.toJSONString(domainIndicators));

            int dataSize = domainIndicators.size();
            List<DomainDesionData> lastDomainDesionData = new ArrayList<>();
            int finalAcceptDataCount = 0;
            if (dataSize >= acceptDataCount) {
                finalAcceptDataCount = acceptDataCount;
            } else {
                finalAcceptDataCount = dataSize;
            }
            int dataIndex = dataSize - 1;
            for (int i = 0; i < finalAcceptDataCount; i++) {
                DomainIndicator domainIndicator = domainIndicators.get(dataIndex --);                               //从后往前取值
                if (domainIndicator != null) {
//                        double nluDomainScore = domainIndicator.getNluDomainScore();
//                        int totalValuedSlotCount = domainIndicator.getTotalValuedSlotCount();
//                        if (nluDomainScore < 1d && totalValuedSlotCount == 0) {
//                            logger.error("2.1.g-2.*.此领域不可用");
//                        } else {
                    String domainIndex = domainIndicator.getDomainIndex();
                    DomainDesionData domainDesionData = domainDesionDatasMap.get(domainIndex);                                       //选择出来的最佳DM的业务数据
                    lastDomainDesionData.add(domainDesionData);
//                        }
                }
            }

            String domainDatasJS = JSON.toJSONString(domainDesionDatasMap);                                                  //按理说这个时候domainDatas里面的各个元素已经被评估等级了（设置了level）
            decisionProcessMap.put(Constant.DOMAIN_DATAS, domainDatasJS);

            String domainIndicatorsJS = JSON.toJSONString(domainIndicators);
            decisionProcessMap.put(Constant.DOMAIN_INDICATORS, domainIndicatorsJS);

            return lastDomainDesionData;

        }
        else {
            logger.error("2.1.g-4.警告！此轮NLU给出了解析结果，但此结果在解析对比过程中产生了异常，所以只能走兜底策略!");
            return null;
        }
    }

    private String[] getDomainIntentTaskByIntentId(Integer intentId, IntentService intentService, List<DialogState> historyDialogStates) {
        String domainName = null;
        String intentName = null;
        String taskName = null;
        Intent intent = intentService.selectByIntentId(intentId);
        if(intent == null){
            intentName = EntityConstant.UNKNOWN_INTENT;
            if(historyDialogStates != null && historyDialogStates.size() > 0) {
                DialogState lastDialogState = historyDialogStates.get(0);
                domainName = lastDialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
                taskName = lastDialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
            }else{
                domainName = EntityConstant.GUIDE_DOMAIN;
                taskName = EntityConstant.NO_TASK;
            }
        }else{
            domainName = intent.getDomainName();
            intentName = intent.getIntentName();
            taskName = getNearestDomainDSTaskName(intent, historyDialogStates);
        }
        return new String[]{domainName, intentName, taskName};
    }

    private String getNearestDomainDSTaskName(Intent intent, List<DialogState> historyDialogStates) {
        String taskName = null;
        String domainName = intent.getDomainName();
        String taskNames = intent.getTaskNames();
        String[] taskNameSplits = taskNames.split(",");
        if(taskNameSplits.length == 1){
            taskName = taskNameSplits[0];
        }else{
            if(historyDialogStates != null && historyDialogStates.size() > 0){
                for (DialogState historyDialogState : historyDialogStates) {
                    String dmDomainNameTmp = historyDialogState.getParamValue(PC.DOMAIN_NAME, Constant.PLATFORM_PARAM);
                    if(dmDomainNameTmp.equals(domainName)){
                        taskName = historyDialogState.getParamValue(PC.TASK_NAME, Constant.PLATFORM_PARAM);
                        break;
                    }
                }
            }
            if(taskName == null) taskName = taskNameSplits[0];
        }
        return taskName;
    }

    /**
     * 将NLU解析得到的槽位数据聚合，得到一个Map
     *
     * @param domainInfo NLU领域数据
     * @return  得到的是Map<nluSlotName, List<SlotInfo>>
     */
    private Map<String, List<SlotInfo>> aggregateNLUSlots(DomainInfo domainInfo) {
        Map<String, List<SlotInfo>> nluSlotInfoListMap = new HashMap<>();
        Map<Integer, SlotInfo> nluSlotInfoMap = domainInfo.getSlots();
        if(nluSlotInfoMap != null){
            for (Integer nluSlotIndex : nluSlotInfoMap.keySet()) {
                SlotInfo slotInfo = nluSlotInfoMap.get(nluSlotIndex);
                String nluSlotName = slotInfo.getName();
                List<SlotInfo> slotInfos = nluSlotInfoListMap.get(nluSlotName);
                if (slotInfos == null) {
                    slotInfos = new ArrayList<>();
                }
                slotInfos.add(slotInfo);
                nluSlotInfoListMap.put(nluSlotName, slotInfos);
            }
        }
        return nluSlotInfoListMap;
    }

    /**
     * 根据当前NLU给的槽位，来决策所得到的槽位中哪些槽位可以被继承（例如外卖的业务：无品类，有属性，则继承；其它一律不继承。）
     *
     * @param targetSlotStates  本轮得到的槽位值集合
     * @param intentId          本轮决定的意图ID
     * @return                  本轮意图下，能被继承的槽位有哪些
     */
    private Set<String> getExtendableSlots(Collection<SlotState> targetSlotStates, Integer intentId, SlotService slotService) {
        boolean extendLastSlots = false;
        List<Slot> slots = slotService.selectByIntentId(intentId);

        Set<String> extendNeededSlotNames = null;
        Set<String> extendForbiddenSlotNames = null;
        if(slots != null && slots.size() > 0){
            extendNeededSlotNames = new HashSet<>();                                                                    //可以继承的槽位
            extendForbiddenSlotNames = new HashSet<>();                                                                 //破坏继承关系的槽位
            for (Slot slot : slots) {
                String slotName = slot.getSlotName();
                byte inheritable = slot.getInheritable();
                if(inheritable == INHERITABLE){
                    extendNeededSlotNames.add(slotName);
                }
                byte inheritDestructible = slot.getInheritDestructible();
                if(inheritDestructible == INHERIT_DESTRUCTIBLE){
                    extendForbiddenSlotNames.add(slotName);
                }
            }

            Set<String> valuedSlotNames = new HashSet<>();                                                              //收集当前有值的槽位名
            if (targetSlotStates != null && targetSlotStates.size() > 0) {
                for (SlotState currentSlotState : targetSlotStates) {
                    String currentSlotName = currentSlotState.getSlotName();
                    List<SlotInfo> currentSlotInfos = currentSlotState.getSlotInfos();
                    if (currentSlotInfos != null && currentSlotInfos.size() > 0) {
                        SlotInfo currentSlotInfo = currentSlotInfos.get(0);
                        if (currentSlotInfo != null && currentSlotInfo.getValue() != null) {                            //并且这个槽位的值非空
                            valuedSlotNames.add(currentSlotName);
                        }
                    }
                }
            }
            if (valuedSlotNames.size() > 0) {
                String extendNeededSlotNamesStr = JSON.toJSONString(extendNeededSlotNames);                             //做交集前先将集合转成字符串
                String extendForbiddenSlotNamesStr = JSON.toJSONString(extendForbiddenSlotNames);

                extendNeededSlotNames.removeAll(valuedSlotNames);                                                       //可继承的槽位，移除了本轮有值槽位后，还剩下的可继承的槽位集（做了差集，留下的都是还需要继承的槽位）
                extendForbiddenSlotNames.retainAll(valuedSlotNames);                                                    //禁止继承关系的槽位集，看看有没有哪个在本轮获取了值（做了交集，看看此类集合是否真的获取了值）

                if (extendNeededSlotNames.size() > 0 && extendForbiddenSlotNames.size() == 0) {
                    extendLastSlots = true;
                }
                logger.debug("2.1.r.是否继承上轮的槽位？本轮有值槽位：" + valuedSlotNames.toString() + " ， " +
                        "需要有的槽位：" + extendNeededSlotNamesStr + "，" +
                        "禁止存在的槽位：" + extendForbiddenSlotNamesStr + "，" +
                        "是否继承：" + extendLastSlots);
            }

        }
        return extendNeededSlotNames;
    }
}
