package com.cooler.ai.platform.action;

import com.cooler.ai.nlg.NlgConstant;
import com.cooler.ai.nlg.entity.GuideOptionInfo;
import com.cooler.ai.nlg.entity.NlgGuidesInfo;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.nlg.facade.NlgFacade;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class BaseTaskAction implements TaskAction<DMRequest, DialogState, BizDataModelState<String>> {

    private static Logger logger = LoggerFactory.getLogger(BaseTaskAction.class);

    @Value("${botName}")
    protected String botName;

    protected DMRequest dmRequest;
    protected DialogState dialogState;
    protected Map<String, BizDataModelState<String>> bizDataMSMap;                                          //此属性专门放置主流程传入的业务数据集，此类为每一个Action提供此数据集的简单访问方式

    @Value("${themeWord}")
    protected String themeWord;
//    protected Properties properties;

    @Resource
    protected NlgFacade nlgFacade;


    //顶层接口TaskAction的方法，为所有Action，包括TaskAction和DataAction准备3类数据源（本系统TaskAction包含DataAction）
    @Override
    public void setData(DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap) {
        this.dmRequest = dmRequest;
        this.dialogState = dialogState;
        this.bizDataMSMap = bizDataMSMap;
    }

//    @PostConstruct
//    public void init() {
//        try {
//            EncodedResource encodedResource = new EncodedResource(new ClassPathResource("properties/subject.properties"), "UTF-8");
//            properties = PropertiesLoaderUtils.loadProperties(encodedResource);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //---------------------------------------------------下面是两个工具方法，在各个Action实现中，可以调用，进而获取本轮或历史轮中各个key下的具体值

    /**
     * 针对Map的工具方法，可以不用一个个的判空了（此方法用在准备查询参数的时候，添加参数，所加的key从各个服务的Constance类里面来）
     * @param paramMap
     * @param key
     * @param value
     */
    public void addToMap(Map<String, String> paramMap, String key, String value){
        if(value != null){
            paramMap.put(key, value);
        }
    }

    public boolean isEmpty(String value){
        if(value == null) return true;
        if(value.equals("") || value.equals("null") || value.equals("[]") || value.equals("{}")) return true;
        return false;
    }

    /**
     * 此方法为各个处理动作和交互动作提供访问PLATFORM_PARAM和CUSTOM_PARAM类型参数的入口。
     * 开发者只需知道某个参数为此两类参数，无需关系是哪一类，就不用DialogState中的getParamValue()方法了。
     * @param paramKey
     * @return
     */
    public String getPCParamValue(String paramKey){
        String value = dialogState.getParamValue(paramKey, Constant.PLATFORM_PARAM);
        if(value == null){
            value = dialogState.getParamValue(paramKey, Constant.CUSTOM_PARAM);
        }
        return value;
    }

    /**
     * 为上面的方法提供一个默认值
     * @param paramKey
     * @param defaultValue
     * @return
     */
    public String getPCParamValueOrDefault(String paramKey, String defaultValue){
        String value = getPCParamValue(paramKey);
        if(value == null){
            value = defaultValue;
        }
        return value;
    }

    /**
     * 此类为各个处理动作和交互动作提供一个访问bizDataMap和BizParam和SlotParam的入口，此方法非常重要！！可以直接访问新旧业务数据和本轮槽位数据。
     * @param bizItemName
     * @return
     */
    public String getBizDataValue(String bizItemName){
        String bizDataValue = null;
        if(bizDataMSMap != null){
            BizDataModelState<String> dataModelState = bizDataMSMap.get(bizItemName);   //先在本轮获得的最新业务数据里面查找所需业务参数
            if(dataModelState != null){
                bizDataValue = dataModelState.getT();
            }
        }
        if(bizDataValue == null){        //可能处理动作中并没有bizItemName指定的新业务参数，那就在老的业务参数里面找，如果还找不到则在槽位参数里面找（这里说找业务参数，为何最后要找到槽位参数呢？因为有时业务参数和槽位参数会同名）
            bizDataValue = dialogState.getParamValue(bizItemName, Constant.BIZ_SLOT_PARAM);
        }
        return bizDataValue;
    }

    /**
     * 上面方法的变体，可以传入一个默认参数，如果没有获取值，则传出默认参数
     * @param bizItemName
     * @param defaultValue
     * @return
     */
    public String getBizDataValueOrDefault(String bizItemName, String defaultValue){
        String bizDataValue = getBizDataValue(bizItemName);
        if(isEmpty(bizDataValue)){
            bizDataValue = defaultValue;
        }
        return bizDataValue;
    }

    public void addToBizDataMSMap(Map<String, BizDataModelState<String>> bizDataMSMap, String bizItemName, String bizItemValue, int keepTurnCount){
        //即使bizItemValue为null，也要加进去，为了覆盖历史轮同名业务数据
        bizDataMSMap.put(bizItemName, new BizDataModelState<>(
                dialogState.getSessionId(),
                dialogState.getTotalTurnNum(),
                dialogState.getDomainName(),
                dialogState.getTaskName(),
                dialogState.getDomainTaskTurnNum(),
                dialogState.getBotName(),
                bizItemName,
                bizItemValue,
                keepTurnCount
                ));
    }

    public void addToBizDataMSMapIfNotNull(Map<String, BizDataModelState<String>> bizDataMSMap, String bizItemName, String bizItemValue, int keepTurnCount){
        //即使bizItemValue为null，也要加进去，为了覆盖历史轮同名业务数据
        if(!isEmpty(bizItemValue)){
            bizDataMSMap.put(bizItemName, new BizDataModelState<>(
                    dialogState.getSessionId(),
                    dialogState.getTotalTurnNum(),
                    dialogState.getDomainName(),
                    dialogState.getTaskName(),
                    dialogState.getDomainTaskTurnNum(),
                    dialogState.getBotName(),
                    bizItemName,
                    bizItemValue,
                    keepTurnCount
            ));
        }
    }

//    /**
//     * 获取属性文件的话术，以及所选话术标签
//     * @param topic
//     * @param transformIntentName
//     * @param intentName
//     * @param scriptCondition
//     * @return
//     */
//    public String[] getPropertiesScriptData(String topic, String transformIntentName, String intentName, String scriptCondition){
//        logger.debug("getPropertiesScriptData方法，入参：topic : {} , transformIntentName : {} , intentName : {} , scriptCondition : {}", topic, transformIntentName, intentName, scriptCondition);
//
//        String bizScriptLabel = topic + "@" + transformIntentName + "@" + intentName + "@" + scriptCondition;
//        String script = properties.getProperty(bizScriptLabel);
//        String scriptLabel = null;
//        if(script != null){
//            scriptLabel = bizScriptLabel;
//        }else{
//            logger.error("管理员的锅：话术未准备，业务话术标签为：" + bizScriptLabel);
//            scriptLabel = "defaultScriptLabel";
//            script = "抱歉啊！我不知道该说什么！";
//        }
//        return new String[]{ scriptLabel, script };
//    }

    protected NlgTemplateInfo getNlgTemplateInfo(Class clazz, String transformIntentName, String intentName, Map<String, String> paramKvs, String themeWord){
        NlgTemplateInfo nlgTemplateInfo = nlgFacade.getNlgSentence(botName, clazz.getSimpleName(), transformIntentName, intentName, paramKvs, themeWord);
        return nlgTemplateInfo;
    }

    protected NlgTemplateInfo getNlgTemplateInfoOfDefaultTheme(Class clazz, String transformIntentName, String intentName, Map<String, String> paramKvs){
        NlgTemplateInfo nlgTemplateInfo =  nlgFacade.getNlgSentence(botName, clazz.getSimpleName(), transformIntentName, intentName, paramKvs, NlgConstant.V_DEFAULT_THEME);
        return nlgTemplateInfo;
    }

    protected List<GuideOptionInfo> getGuideOptionInfos(Integer nlgId){
        List<GuideOptionInfo> guideOptionInfos = nlgFacade.getGuideOptions(nlgId);
        return guideOptionInfos;
    }

    protected NlgGuidesInfo getNlgGuidesInfo(Class clazz, String transformIntentName, String intentName, Map<String, String> paramKvs, String themeWord){
        NlgGuidesInfo nlgGuidesInfo = nlgFacade.getNlgGuidesInfo(botName, clazz.getSimpleName(), transformIntentName, intentName, paramKvs, themeWord);
        return nlgGuidesInfo;
    }

    protected NlgGuidesInfo getNlgGuidesInfoOfDefaultTheme(Class clazz, String transformIntentName, String intentName, Map<String, String> paramKvs){
        NlgGuidesInfo nlgGuidesInfo =  nlgFacade.getNlgGuidesInfo(botName, clazz.getSimpleName(), transformIntentName, intentName, paramKvs, NlgConstant.V_DEFAULT_THEME);
        return nlgGuidesInfo;
    }
}
