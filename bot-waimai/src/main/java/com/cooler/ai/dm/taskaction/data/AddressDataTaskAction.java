package com.cooler.ai.dm.taskaction.data;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.action.BaseDataTaskAction;
import com.cooler.ai.platform.facade.BizDataFacade;
import com.cooler.ai.platform.facade.constance.CC;
import com.cooler.ai.platform.model.AddressInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/1/3
 **/
@Component("addressDataTaskAction")
public class AddressDataTaskAction extends BaseDataTaskAction {

    @Resource
    private BizDataFacade bizDataFacade;

    @Override
    public String getParamValue() {

        String userId = getPCParamValue(CC.USER_ID);
        List<AddressInfo> addressInfos = bizDataFacade.getAddressesByUserId(userId);
        if(addressInfos == null || addressInfos.size() == 0)    return null;
        AddressInfo addressInfo = null;
        for (AddressInfo addressInfoTmp : addressInfos) {
            if(addressInfoTmp.getIsDefault() == 1){
                addressInfo = addressInfoTmp;
                break;
            }
        }
        if(addressInfo == null){
            addressInfo = addressInfos.get(0);
        }
        String addressInfoJS = JSON.toJSONString(addressInfo);
        return addressInfoJS;
    }


}
