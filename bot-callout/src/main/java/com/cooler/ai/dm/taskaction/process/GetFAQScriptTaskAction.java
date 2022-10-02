package com.cooler.ai.dm.taskaction.process;

import com.cooler.ai.dm.constant.BC;
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
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("getFAQScriptTaskAction")
public class GetFAQScriptTaskAction extends BaseInnerProcessedTaskAction {
    private Logger logger = LoggerFactory.getLogger(GetFAQScriptTaskAction.class);

    @Override
    public Map<String, BizDataModelState<String>> process() {
        logger.info("GetFAQScriptTaskAction  （处理动作）获取FAQ话术");

        //1.（准备Intent值）     获取当前识别到的Intent
        String transformIntentName = getPCParamValueOrDefault(PC.TRANSFORM_INTENT_NAME, EntityConstant.NO_INTENT);
        String intentName = getPCParamValueOrDefault(PC.INTENT_NAME, EntityConstant.NO_INTENT);

        //2.设计好传出的数据
        String scriptLabel = null;
        String script = null;

        //3.根据不同intentName，获取传出的业务数据（包括：1.槽位数据和合成数据的准备；2.业务数据的准备；）
        scriptLabel = "none";
        NlgGuidesInfo nlgGuidesInfo = getNlgGuidesInfoOfDefaultTheme(this.getClass(), transformIntentName, intentName, null);
        NlgTemplateInfo nlgTemplateInfo = nlgGuidesInfo.getNlgTemplateInfo();
        script = nlgTemplateInfo.getNlgTemplate();
        String lastReply = getBizDataValue(Constant.REPLY);

        script = script + lastReply;

        //4.包装传出的业务数据
        Map<String, BizDataModelState<String>> bizDataMapTmp = new HashMap<>();
        addNlgGuidesInfoToMap(bizDataMapTmp, nlgGuidesInfo, Constant.ONE);

        addToBizDataMSMap(bizDataMapTmp, BC.SCRIPT_LABEL, scriptLabel, Constant.ZERO);
        addToBizDataMSMap(bizDataMapTmp, BC.SCRIPT, script, Constant.ZERO);

        return bizDataMapTmp;
    }

    @Override
    public String routeNextProcessCode() {
        return "reportScript";
    }
}
