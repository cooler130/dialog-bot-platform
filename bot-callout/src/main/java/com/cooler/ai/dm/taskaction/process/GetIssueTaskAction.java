package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.GuideOptionInfo;
import com.cooler.ai.nlg.entity.NlgGuidesInfo;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.model.EntityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("getIssueTaskAction")
public class GetIssueTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetIssueTaskAction.class);

    private final int START = 1;
    private final int ASK_QUESTION = 5;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetIssueTaskAction  （处理动作）获取主题语");

        //1.（准备Intent值）     获取当前识别到的Intent
        String transformIntentName = getPCParamValueOrDefault(PC.TRANSFORM_INTENT_NAME, EntityConstant.NO_INTENT);
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        Integer issueIndex = null;
        String question = null;
        String issue = null;
        String nlgTemplateInfoJS = null;
        String guideOptionsJS = null;

        String fromStateIdStr = getPCParamValue(PC.FROM_STATE_ID);
        int fromStateId = Integer.parseInt(fromStateIdStr);

        String repeatCountStr = getBizDataValueOrDefault(BC.REPEAT_COUNT, "0");
        int repeatCount = Integer.parseInt(repeatCountStr);

        String unknownCountStr = getBizDataValueOrDefault(BC.UNKNOWN_COUNT, "0");
        int unknownCount = Integer.parseInt(unknownCountStr);

        String lastIssueIndexStr = getBizDataValueOrDefault(BC.ISSUE_INDEX, "0");
        int lastIssueIndex = Integer.parseInt(lastIssueIndexStr);

        String issueQAMapJs = getBizDataValue(BC.ISSUE_QA_MAP);

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        switch (transformIntentName){
            case "repeat" : {
                if(fromStateId == START){
                    issueIndex = lastIssueIndex + 1;
                }
                repeatCount ++;
                break;
            }
            case "unknown_intent" : {
                issueIndex = lastIssueIndex + 1;
                unknownCount ++;
                break;
            }
            default: {
                issueIndex = lastIssueIndex + 1;
            }
        }
        Map<String, String> paramKvs = new HashMap<>();
        paramKvs.put(NlgConstant.P_TOPIC_WORD, "q_" + issueIndex);

        NlgGuidesInfo nlgGuidesInfo = getNlgGuidesInfoOfDefaultTheme(this.getClass(), NlgConstant.V_NONE_VALUE, NlgConstant.V_NONE_VALUE, paramKvs);
        NlgTemplateInfo nlgTemplateInfo = nlgGuidesInfo.getNlgTemplateInfo();
        question = nlgTemplateInfo.getNlgTemplate();

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addNlgGuidesInfoToMap(bizDataMapTmp, nlgGuidesInfo, Constant.ONE);                                  //直接准备话术和引导项的数据

        addToBizDataMSMap(bizDataMapTmp, BC.ISSUE_INDEX, issueIndex + "", Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.QUESTION, question, Constant.FOREVER);
        addToBizDataMSMap(bizDataMapTmp, BC.REPEAT_COUNT, repeatCount + "", Constant.ONE);
        addToBizDataMSMap(bizDataMapTmp, BC.UNKNOWN_COUNT, unknownCount + "", Constant.ONE);
        addToBizDataMSMap(bizDataMapTmp, BC.ISSUE_QA_MAP, issueQAMapJs, Constant.FOREVER);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }
}
