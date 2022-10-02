package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.platform.action.BaseDataTaskAction;

import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.facade.BuRouterDataFacade;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.model.TQDataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("tqBeliefDataTaskAction")
public class TqBeliefDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(TqBeliefDataTaskAction.class);

    @Resource
    private BuRouterDataFacade buRouterDataFacade;


    @Override
    public void preprocess() {
        Transition transition = this.getTransition();
        String transitionName = transition.getTransitionName();
        String intentName = getPCParamValue(PC.INTENT_NAME);

        switch (intentName) {
            case "positive":{
                String tqDataStr = getBizDataValueOrDefault(BC.TQ_DATA, "");
                List<TQDataInfo> tqDataInfos = JSON.parseArray(tqDataStr, TQDataInfo.class);
                if(tqDataInfos != null && tqDataInfos.size() > 0){
                    tqDataInfos.get(0).setBelief(1.0f);
                    addToPreconditionDataMap(BC.TQ_DATA, JSON.toJSONString(tqDataInfos));
                    addToPreconditionDataMap(BC.TQ_BELIEF, "1");
                }
                break;
            }
            case "negative":{
                String tqDataStr = getBizDataValueOrDefault(BC.TQ_DATA, "");
                List<TQDataInfo> tqDataInfos = JSON.parseArray(tqDataStr, TQDataInfo.class);
                if(tqDataInfos != null && tqDataInfos.size() > 0){
                    tqDataInfos.remove(0);
                    addToPreconditionDataMap(BC.TQ_DATA, JSON.toJSONString(tqDataInfos));
                    addToPreconditionDataMap(BC.TQ_BELIEF, "0");
                }
                break;
            }
            case "express_question": {
                String originalSentence = getPCParamValueOrDefault(PC.SENTENCE, "");
                List<TQDataInfo> tqDataList = buRouterDataFacade.getTqDatas(originalSentence);
                Collections.sort(tqDataList, new Comparator<TQDataInfo>() {
                    @Override
                    public int compare(TQDataInfo o1, TQDataInfo o2) {
                        if(o1.getBelief() > o2.getBelief()) return -1;
                        else if(o1.getBelief() == o2.getBelief()) return 0;
                        else return 1;
                    }
                });

                List<TQDataInfo> tqDataInfos = new ArrayList<>();
                if(tqDataList != null && tqDataList.size() > 0){
                    tqDataInfos.add(tqDataList.get(0));
                }
                if(tqDataList != null && tqDataList.size() > 1){
                    tqDataInfos.add(tqDataList.get(1));
                }
                addToPreconditionDataMap(BC.TQ_DATA, JSON.toJSONString(tqDataInfos));
                if(tqDataInfos.size() > 0) addToPreconditionDataMap(BC.TQ_BELIEF, tqDataInfos.get(0).getBelief() + "");
                break;
            }
        }
    }

    @Override
    public String getParamValue() {
        String tqBeliefStr = getFromPreconditionDataMap(BC.TQ_BELIEF);
        return tqBeliefStr == null ? "none" : tqBeliefStr;
    }

}
