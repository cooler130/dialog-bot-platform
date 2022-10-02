package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.dm.constant.CC2;
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

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("getByeScriptTaskAction")
public class GetByeScriptTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetByeScriptTaskAction.class);

//    private final String TOPIC = "bye";
//    private final Integer REQUIRY_RESULT = 6;

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetByeScriptTaskAction  （处理动作）获取结束语");

        //1.（准备Intent值）     获取当前识别到的Intent
        String transformIntentName = getPCParamValueOrDefault(PC.TRANSFORM_INTENT_NAME, EntityConstant.NO_INTENT);
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.（准备槽位值）        获取所涉及到的槽位值
        String signal = getPCParamValue(CC2.SIGNAL);

        String fromStateIdStr = getPCParamValueOrDefault(PC.FROM_STATE_ID, EntityConstant.START_STATE_ID + "");

        String unReportIssueCountStr = getBizDataValueOrDefault(BC.UNREPORT_ISSUE_COUNT, "0");
        int unReportIssueCount = Integer.parseInt(unReportIssueCountStr);

        String timeoutCountStr = getBizDataValueOrDefault(BC.TIMEOUT_COUNT, "0");
        int timeoutCount = Integer.parseInt(timeoutCountStr);

        //3.设计好传出的数据
        String scriptLabel = null;
        String byeScript = null;
        String issueQAMapJs = null;

        //4.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        Map<String, String> paramKvs = new HashMap<>();
        paramKvs.put(PC.TRANSFORM_INTENT_NAME, transformIntentName);
        paramKvs.put(PC.INTENT_NAME, intentName);

        paramKvs.put(CC2.SIGNAL, signal);
        paramKvs.put(BC.TIMEOUT_COUNT, timeoutCount >= 2 ? "timeout_2+" : timeoutCountStr);
        paramKvs.put(BC.UNREPORT_ISSUE_COUNT, unReportIssueCount > 0 ? unReportIssueCountStr : "unReportIssueCount_0-");
        paramKvs.put(PC.FROM_STATE_ID, fromStateIdStr);

        String[] excludeIntents = { "no_intent", "negative", "refuse", "repeat", "faq" };
        Set<String> excludeIntentSet = new HashSet<>(Arrays.asList(excludeIntents));
        paramKvs.put("containExcludeIntents", excludeIntentSet.contains(transformIntentName) ? "true" : "false");

        scriptLabel = JSON.toJSONString(paramKvs);
        NlgGuidesInfo nlgGuidesInfo = getNlgGuidesInfoOfDefaultTheme(this.getClass(), transformIntentName, intentName, paramKvs);

        issueQAMapJs = getIssueQAMapJs();

        //5.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addNlgGuidesInfoToMap(bizDataMapTmp, nlgGuidesInfo, Constant.ONE);

        addToBizDataMSMap(bizDataMapTmp, BC.SCRIPT_LABEL, scriptLabel, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.ISSUE_QA_MAP, issueQAMapJs, Constant.FOREVER);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }

    private String getIssueQAMapJs(){
        String issueQAMapJs = getBizDataValue(BC.ISSUE_QA_MAP);
        String lastIssueIndexStr = getBizDataValueOrDefault(BC.ISSUE_INDEX, "0");
        int lastIssueIndex = Integer.parseInt(lastIssueIndexStr);

        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfo(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, null, lastIssueIndex + "");
        String lastQuestion = nlgTemplateInfo.getNlgTemplate();
        String lastAnswer = getPCParamValue(PC.SENTENCE);

        Map<String, String> issueQAMap = JSON.parseObject(issueQAMapJs, Map.class);
        if(issueQAMap == null || issueQAMap.size() == 0)  issueQAMap = new HashMap<>();
        if(lastAnswer != null && !lastAnswer.equals("")) issueQAMap.put(lastQuestion, lastAnswer);
        issueQAMapJs = JSON.toJSONString(issueQAMap);
        return issueQAMapJs;
    }
}
