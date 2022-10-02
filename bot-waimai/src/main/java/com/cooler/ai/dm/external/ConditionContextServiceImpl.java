package com.cooler.ai.dm.external;

import com.cooler.ai.platform.action.DataTaskAction;
import com.cooler.ai.platform.entity.ConditionLogic;
import com.cooler.ai.platform.entity.ConditionRule;
import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;
import com.cooler.ai.platform.model.BaseConditionData;
import com.cooler.ai.platform.model.ConditionData;
import com.cooler.ai.platform.service.external.ConditionContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/21
 **/
public class ConditionContextServiceImpl implements ConditionContextService {

    private Logger logger = LoggerFactory.getLogger(ConditionContextServiceImpl.class);

    @Autowired
    private Map<String, DataTaskAction> dataTaskActionMap = null;

    public Map<String, DataTaskAction> getDataTaskActionMap() {
        return dataTaskActionMap;
    }

    public void setDataTaskActionMap(Map<String, DataTaskAction> dataTaskActionMap) {
        this.dataTaskActionMap = dataTaskActionMap;
    }


    /**
     * 下面针对5类数据构造ConditionData：
     * 其中SLOT_PARAM、CUSTOM_PARAM、PLATFORM_PARAM，都可以构建自己的DataTaskAction对数据进行预处理和改写，如果在bot中没有这3类数据的DataTaskAction，则用DS中默认的变量数据，它们的DataTaskAction不强制设置（即有则用，没有则不用），这样增强了对数据处理的灵活性。
     * 而BIZ_PARAM作为业务数据，是各个数据的合成数据，每一轮都会变化，所以要强制设置DataTaskAction（即必须要有），如果没有则默认为null值
     * @param transition
     * @param conditionRule
     * @param conditionLogic
     * @param dmRequest
     * @param dialogState
     * @param bizDataMSMap
     * @param isHadBePreprocessed
     * @return
     */
    @Override
    public ConditionData getConditionData(Transition transition, ConditionRule conditionRule, ConditionLogic conditionLogic, DMRequest dmRequest, DialogState dialogState, Map<String, BizDataModelState<String>> bizDataMSMap, boolean isHadBePreprocessed) {
        Integer taskId = conditionRule.getTaskId();
        String paramsName = conditionRule.getParamName();
        Integer paramsType = conditionRule.getParamType();
        ConditionData conditionData = null;

        String paramValue = null;
        Map<String, String> dataMap = new HashMap<>();
        switch (paramsType) {
            case Constant.NULL_PARAM: {

                break;
            }
            case Constant.SLOT_PARAM: {             //在slotOption阶段加入到ParamValueMap中
                paramValue = dialogState.getParamValue(paramsName, Constant.SLOT_PARAM);
                break;
            }
            case Constant.CUSTOM_PARAM:{            //在slotOption阶段加入到ParamValueMap中
                paramValue = dialogState.getParamValue(paramsName, Constant.CUSTOM_PARAM);
                break;
            }
            case Constant.PLATFORM_PARAM: {         //在slotOption阶段加入到ParamValueMap中
                paramValue = dialogState.getParamValue(paramsName, Constant.PLATFORM_PARAM);
                break;
            }
            case Constant.BIZ_PARAM: {              //此过程获得的参数作为BIZ_PARAM，传出后会在DST阶段加入到ParamValueMap中（此类变量提供了扩展性：如果一类参数在事前不能明确，则可以放到此类型中）
                dataMap = getParamValueFromDataTaskAction(paramsName, paramsType, transition, dmRequest, dialogState, isHadBePreprocessed, bizDataMSMap);
                paramValue = dataMap.get(paramsName);
//                //下面的这句暂时注释掉，因为这个业务数据现在还不能加入到bizDataMSMap中，bizDataMSMap是为ProcessedTaskAction准备存放业务数据的容器，这里只是获取评判数据的过程，获取的业务数据后续会放到DS的paramValueMap中。
                //与此相对，在各个DataTaskAction中的preprocess()方法，是由于可能对既往的业务数据进行了修改，一但修改，就应该在DS的paramValueMap和bizDataMSMap中进行更新（没有办法的举动），理论上整个DST阶段都不应该加业务数据到bizDataMSMap中。
//                bizDataMSMap.put(paramsName, new BizDataModelState<>(dialogState.getDmName(), dmRequest.getSessionId(), dmRequest.getTurnNum(), paramsName, paramValue, Constant.ZERO));
                break;
            }
        }
        conditionData = new BaseConditionData(conditionRule, conditionLogic, paramsType, paramsName, paramValue, 1f, null, true, dataMap);
        return conditionData;
    }

    private Map<String, String> getParamValueFromDataTaskAction(String paramName, Integer paramsType, Transition transition, DMRequest dmRequest, DialogState dialogState, boolean hadBePreprocessed, Map<String, BizDataModelState<String>> bizDataMSMap) {
        String paramValue = null;
        DataTaskAction dataTaskAction = dataTaskActionMap.get(paramName);
        Map<String, String> datasMap = new HashMap<>();
        if(dataTaskAction != null){
            dataTaskAction.setData(dmRequest, dialogState, bizDataMSMap);
            dataTaskAction.setTransition(transition);
            if(!hadBePreprocessed){
                dataTaskAction.preprocess();                                                                            //没有预处理则先要进行预处理（hadBePreprocessed变量来控制每一个param的这个预处理过程在一轮对话中只有一遍，所以复杂的过程写在这个预处理工作中，这个方法如果更新了）
            }
            paramValue = dataTaskAction.getParamValue();                                                                //预处理之后，则调用DataTaskAction进行数据创建并返回出来。（这个变量值获取过程可能会有多遍）
            dialogState.addToParamValueMap(paramName, paramValue, paramsType);                                         //上面4种类型的数据都会经过这个方法，如果此方法获取了新数据，就要在这个时候更新设置到DS中的paramValueMap中
            datasMap = dataTaskAction.getPreconditionDatasMap();
            datasMap.put(paramName, paramValue);
        }else{
            if(paramsType == Constant.BIZ_PARAM){
                logger.error("警告！业务数据{}，没有配置相关的DataTaskAction，进而无法产生此数据，请联系管理员进行配置！", paramName);
            }
        }
        return datasMap;
    }
}
