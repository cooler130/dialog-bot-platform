package com.cooler.ai.platform.action;

import com.cooler.ai.platform.entity.Transition;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.BizDataModelState;
import com.cooler.ai.platform.facade.model.DMRequest;
import com.cooler.ai.platform.facade.model.DialogState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseDataTaskAction extends BaseTaskAction implements DataTaskAction<DMRequest, DialogState, BizDataModelState<String>>  {

    private Transition transition;

    private Map<String, String> preconditionDatasMap = new ConcurrentHashMap<>();       //此Map放置一些前提数据，在preprocess()中准备这些前提数据，在getParamValue()中取出来使用

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    public Map<String, String> getPreconditionDatasMap() {
        return preconditionDatasMap;
    }

    //数据在获取之前的预处理工作，预处理工作不一定只对当前数据本身预处理，整个DS的参数都可以预处理，处理后当前数据可能会受到影响（有必要则实现，无必要则不用管它，由各个子类重写来实现）
    @Override
    public void preprocess() {  }

    //业务数据获取，则由各个子类重写来实现
    @Override
    public String getParamValue() {
        return null;
    }

    /**
     * 工具方法，如果在preprocess方法中产生了新的数据（可能是业务数据，需要在preprocess()方法中调用此方法来更新DS中和bizDataMSMap中的数据
     * @param paramsName
     * @param paramValue
     * @param keepTurnCount
     */
    public void addToParamValueAndBizDataMap(String paramsName, String paramValue, Integer paramType, int keepTurnCount){
        dialogState.addToParamValueMap(paramsName, paramValue, paramType);

        //这里是新产生的业务数据，需要将其保持到业务数据池中.
        if(paramType == Constant.BIZ_PARAM){
            bizDataMSMap.put(paramsName, new BizDataModelState<>(
                    dialogState.getSessionId(),
                    dialogState.getTotalTurnNum(),
                    dialogState.getDomainName(),
                    dialogState.getTaskName(),
                    dialogState.getDomainTaskTurnNum(),
                    dialogState.getBotName(),
                    paramsName,
                    paramValue,
                    keepTurnCount));
        }
    }

    /**
     * 此方法暂时将一些kv存入到本类的preconditionDatasMap中，可以从process()方法传入getParamValue()方法中
     * @param paramName
     * @param paramValue
     */
    public void addToPreconditionDataMap(String paramName, String paramValue){
        preconditionDatasMap.put(paramName, paramValue);
    }

    /**
     * 此方法从本类的preconditionDatasMap取出暂存数据
     * @param paramName
     * @return
     */
    public String getFromPreconditionDataMap(String paramName){
        return preconditionDatasMap.get(paramName);
    }

}
