package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.dm.constant.SC;
import com.cooler.ai.dm.external.DataServiceUtil;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.model.BuDataInfo;
import com.cooler.ai.platform.model.OrderDataInfo;
import com.cooler.ai.platform.model.TQDataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("buBeliefDataTaskAction")
public class BuBeliefDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(BuBeliefDataTaskAction.class);

    @Resource
    private DataServiceUtil dataServiceUtil;

    @Override
    public void preprocess() {
        Transition transition = this.getTransition();
        String transitionName = transition.getTransitionName();
        String intentName = getPCParamValue(PC.INTENT_NAME);

        String slot_bu = getPCParamValue(SC.BU);

        String originalSentence = getPCParamValueOrDefault(PC.SENTENCE, "");

        String fixTqDataJS = getPCParamValueOrDefault(BC.FIXED_TQ_DATA, "");
        TQDataInfo fixedTqDataInfo = JSON.parseObject(fixTqDataJS, TQDataInfo.class);

        String fixOrderDataJS = getPCParamValueOrDefault(BC.FIXED_ORDER_DATA, "");
        OrderDataInfo fixedOrderDataInfo = JSON.parseObject(fixOrderDataJS, OrderDataInfo.class);

        switch (intentName) {
            case "positive":{                                                   //这里的肯定是肯定的当前最大分值的，一般只有一个业务时用户会说肯定
                String buDataStr = getBizDataValueOrDefault(BC.BU_DATA, "");
                List<BuDataInfo> buDataInfos = JSON.parseArray(buDataStr, BuDataInfo.class);
                if(buDataInfos != null && buDataInfos.size() > 0) {
                    buDataInfos.get(0).setBelief(1.0f);
                    addToPreconditionDataMap(BC.BU_DATA, JSON.toJSONString(buDataInfos));
                    addToPreconditionDataMap(BC.BU_BELIEF, "1");
                }
                break;
            }
            case "negative":{                                                   //这里的否定是 "都不是" 的含义
                String buDataStr = getBizDataValueOrDefault(BC.BU_DATA, "");
                List<BuDataInfo> buDataInfos = JSON.parseArray(buDataStr, BuDataInfo.class);
                if(buDataInfos == null) buDataInfos = new ArrayList<>();

                List<BuDataInfo> buDataInfoAsked = new ArrayList<>();
                List<BuDataInfo> newbuDataInfos = new ArrayList<>(buDataInfos);             //可移除
                for (int i = 0; i < 3; i++) {
                    if(newbuDataInfos.size() > 0) {
                        buDataInfoAsked.add(newbuDataInfos.get(0));
                        newbuDataInfos.remove(0);
                    }
                }
                //todo：这里还要记录问过的bu到一个List里面保存起来
                if(newbuDataInfos.size() == 0){
                    newbuDataInfos = dataServiceUtil.getBuDataInfos(originalSentence, fixedTqDataInfo, fixedOrderDataInfo, buDataInfoAsked);
                }
                addToPreconditionDataMap(BC.BU_DATA, JSON.toJSONString(newbuDataInfos));
                addToPreconditionDataMap(BC.BU_BELIEF, "0");
                break;
            }
            case "express_business": {
                //如果是表达业务，则把表达的业务传入，变成BuDataInfo解析结果
                List<BuDataInfo> buDataInfos = dataServiceUtil.getBuDataInfos(originalSentence, fixedTqDataInfo, fixedOrderDataInfo, new ArrayList<BuDataInfo>());
                Collections.sort(buDataInfos, new Comparator<BuDataInfo>() {
                    @Override
                    public int compare(BuDataInfo o1, BuDataInfo o2) {              //降序排列
                        if(o1.getBelief() > o2.getBelief()) return -1;
                        else if(o1.getBelief() < o2.getBelief()) return 1;
                        else return 0;
                    }
                });
                addToPreconditionDataMap(BC.BU_DATA, JSON.toJSONString(buDataInfos));
                if(buDataInfos.size() > 0) addToPreconditionDataMap(BC.BU_BELIEF, buDataInfos.get(0).getBelief() + "");
                break;
            }
            case "select" : {
                String buDataStr = getBizDataValueOrDefault(BC.BU_DATA, "");
                List<BuDataInfo> buDataInfos = JSON.parseArray(buDataStr, BuDataInfo.class);
                String optionNumber = dialogState.getParamValue(SC.OPTION_NUMBER, Constant.SLOT_PARAM);
//                String optionNumber = getPCParamValue(SC.OPTION_NUMBER);
                if(optionNumber == null) return;
                BuDataInfo targetBuData = dataServiceUtil.getTargetBuData(optionNumber, buDataInfos);

                addToPreconditionDataMap(BC.BU_DATA, JSON.toJSONString(Arrays.asList(targetBuData)));
                if(targetBuData != null) addToPreconditionDataMap(BC.BU_BELIEF, "1");
            }
        }
    }

    @Override
    public String getParamValue() {
        String buBeliefStr = getFromPreconditionDataMap(BC.BU_BELIEF);
        return buBeliefStr == null ? "none" : buBeliefStr;
    }

}
