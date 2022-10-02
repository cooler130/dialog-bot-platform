package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.GuideOptionInfo;
import com.cooler.ai.nlg.entity.NlgGuidesInfo;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
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
@Component("getTimeoutScriptTaskAction")
public class GetTimeoutScriptTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetTimeoutScriptTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetRepeatScriptTaskAction  （处理动作）重复请求");
        //1.（准备Intent值）     获取当前识别到的Intent

        //2.设计好传出的数据
        String timeoutScript = null;
        String guideOptionsJS = null;
        String timeoutCountStr = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        String lastReply = getBizDataValue(Constant.REPLY);                                                     //reply是一个特殊的业务变量
        timeoutCountStr = getBizDataValueOrDefault(BC.TIMEOUT_COUNT, "0");
        int timeoutCount = Integer.parseInt(timeoutCountStr) + 1;

        Map<String, String> paramKvs = new HashMap<>();
        paramKvs.put(BC.TIMEOUT_COUNT, timeoutCount + "");
        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), NlgConstant.V_NONE_VALUE, NlgConstant.V_NONE_VALUE, paramKvs);
        timeoutScript = nlgTemplateInfo.getNlgTemplate();

        if(timeoutCount == 1){
            timeoutScript = timeoutScript + lastReply;
        }else{
            paramKvs.put(BC.TIMEOUT_COUNT, (timeoutCount - 1) + "");
            NlgTemplateInfo nlgTemplateInfo1 = getNlgTemplateInfoOfDefaultTheme(this.getClass(), NlgConstant.V_NONE_VALUE, NlgConstant.V_NONE_VALUE, paramKvs);
            String lastTimeoutScript = nlgTemplateInfo1.getNlgTemplate();

            timeoutScript = timeoutScript + lastReply.replace(lastTimeoutScript, "");
        }

        String lastNlgTemplateInfoJS = getBizDataValue(BC.NLG_TEMPLATE_INFO_JS);
        NlgTemplateInfo lastNlgTemplateInfo = JSON.parseObject(lastNlgTemplateInfoJS, NlgTemplateInfo.class);

        String lastGuideOptionInfosJS = getBizDataValue(BC.GUIDE_OPTIONS_JS);
        List<GuideOptionInfo> lastGuideOptionInfos = JSON.parseArray(lastGuideOptionInfosJS, GuideOptionInfo.class);

        NlgGuidesInfo lastNlgGuidesInfo = new NlgGuidesInfo(lastNlgTemplateInfo, lastGuideOptionInfos);

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addNlgGuidesInfoToMap(bizDataMapTmp, lastNlgGuidesInfo, Constant.ONE);                                          //先加载历史的话术-引导集合

        addToBizDataMSMap(bizDataMapTmp, BC.SCRIPT, timeoutScript, Constant.ZERO);                                      //再用本轮得到的script来覆盖话术
        addToBizDataMSMap(bizDataMapTmp, BC.TIMEOUT_COUNT, timeoutCount + "", Constant.ONE);                 //这个记忆1轮

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }
}
