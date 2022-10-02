package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.constance.PC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("unReportIssueCountDataTaskAction")
public class UnReportIssueCountDataTaskAction extends BaseDataTaskAction {
    private Logger logger = LoggerFactory.getLogger(UnReportIssueCountDataTaskAction.class);

    @Value("${issueCount}")
    private String issueCountStr;

    private final int START = 1;
    private final int ASK_QUESTION = 5;
    private final Set<String> START_UNRECORD_INTENTS = new HashSet<>(Arrays.asList("unknown_intent", "positive", "normal", "repeat"));
    private final Set<String> ASK_QUESTION_UNRECORD_INTENTS = new HashSet(Arrays.asList("repeat", "negative", "refuse"));

    @Override
    public void preprocess() {
        String intentName = getPCParamValue(PC.INTENT_NAME);
        String lastIssueIndexStr = getBizDataValueOrDefault(BC.ISSUE_INDEX, "0");
        int lastIssueIndex = Integer.parseInt(lastIssueIndexStr);

        String fromStateIdStr = getPCParamValue(PC.FROM_STATE_ID);
        int fromStateId = Integer.parseInt(fromStateIdStr);

        if((fromStateId == START && START_UNRECORD_INTENTS.contains(intentName)) ||
                fromStateId == ASK_QUESTION && !ASK_QUESTION_UNRECORD_INTENTS.contains(intentName)){

//            Map<String, String> paramKvs = new HashMap<>();
//            paramKvs.put(NlgConstant.P_TOPIC_WORD, "q_" + lastIssueIndex);
//            String lastQuestion = getNlgSentenceOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs);
            String lastQuestion = getBizDataValue(BC.QUESTION);
            String lastAnswer = getPCParamValue(PC.SENTENCE);

            String issueQAMapJs = getBizDataValue(BC.ISSUE_QA_MAP);
            Map<String, String> issueQAMap = JSON.parseObject(issueQAMapJs, Map.class);
            if(issueQAMap == null) issueQAMap = new HashMap<>();
            if(lastQuestion != null && !lastQuestion.equals("") && lastAnswer != null && !lastAnswer.equals("")) issueQAMap.put(lastQuestion, lastAnswer);
            issueQAMapJs = JSON.toJSONString(issueQAMap);

            addToParamValueAndBizDataMap(BC.ISSUE_QA_MAP, issueQAMapJs, Constant.BIZ_PARAM, Constant.FOREVER);
        }
    }

    @Override
    public String getParamValue() {
        String issueQAMapJs = getBizDataValue(BC.ISSUE_QA_MAP);
        Map<String, String> issueQAMap = JSON.parseObject(issueQAMapJs, Map.class);
        int issueCount = Integer.parseInt(issueCountStr);
        if(issueQAMap != null){
            int currentCollectedCount = issueQAMap.size();
            int unReportIssueCount = issueCount - currentCollectedCount;
            return unReportIssueCount + "";
        }
        return issueCount + "";
    }

}
