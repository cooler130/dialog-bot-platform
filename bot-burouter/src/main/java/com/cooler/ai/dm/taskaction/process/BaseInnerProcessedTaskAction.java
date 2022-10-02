package com.cooler.ai.dm.taskaction.process;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.entity.GuideOptionInfo;
import com.cooler.ai.nlg.entity.NlgGuidesInfo;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseProcessedTaskAction;
import com.cooler.ai.platform.facade.model.BizDataModelState;

import java.util.List;
import java.util.Map;

/**
 * 此类承接框架中的BaseProcessedTaskAction，又被各个bot的 XXXProcessedTaskAction 继承。
 * 为何不让各个bot的 XXXProcessedTaskAction 类直接继承 BaseProcessedTaskAction 呢？ 因为在各个bot内部有自己的业务参，如下面的BC.SCRIPT、BC.NLG_TEMPLATE_INFO_JS、BC.GUIDE_OPTIONS_JS
 * 这些业务参数是不适合写入框架层的（框架不该管各个bot内呼的业务内容），那么就要有个中间父类来放置这些涉及到用到bot内部的公共方法，就是此类了。
 */
public class BaseInnerProcessedTaskAction extends BaseProcessedTaskAction {

    /**
     * 在各个XXXProcessedTaskAction类都要准备 BC.SCRIPT、BC.NLG_TEMPLATE_INFO_JS、BC.GUIDE_OPTIONS_JS 这3个话术和引导相关的业务数据，此方法直接包装这个准备过程。
     * @param bizDataMapTmp
     * @param nlgGuidesInfo
     * @param keepTurnCount
     */
    protected void addNlgGuidesInfoToMap(Map<String, BizDataModelState<String>> bizDataMapTmp, NlgGuidesInfo nlgGuidesInfo, int keepTurnCount) {

    }
}
