package com.cooler.ai.dm.external;

import com.cooler.ai.platform.facade.BuRouterDataFacade;
import com.cooler.ai.platform.model.BuDataInfo;
import com.cooler.ai.platform.model.OrderDataInfo;
import com.cooler.ai.platform.model.TQDataInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class DataServiceUtil {
    @Resource
    private BuRouterDataFacade buRouterDataFacade;

    public List<BuDataInfo> getBuDataInfos(String originalSentence, TQDataInfo fixedTqDataInfo, OrderDataInfo fixedOrderDataInfo, List<BuDataInfo> askedBuDataInfos){
        List<BuDataInfo> buDataList = buRouterDataFacade.getBuDatas(originalSentence, fixedTqDataInfo, fixedOrderDataInfo, askedBuDataInfos);
        Collections.sort(buDataList, new Comparator<BuDataInfo>() {
            @Override
            public int compare(BuDataInfo o1, BuDataInfo o2) {
                if(o1.getBelief() > o2.getBelief()) return -1;
                else if(o1.getBelief() == o2.getBelief()) return 0;
                else return 1;
            }
        });
        List<BuDataInfo> buDataInfos = new ArrayList<>();
        if(buDataList != null && buDataList.size() > 0){                //将查出来的业务都加入到 BU_DATA 中
            for (BuDataInfo buDataInfo : buDataList) {
                buDataInfos.add(buDataInfo);
            }
        }
        return buDataInfos;
    }

    public List<BuDataInfo> getProbableBuDatas(String userId, TQDataInfo fixedTqDataInfo){
        List<BuDataInfo> probableBuDatas = buRouterDataFacade.getProbableBuDatas(userId, fixedTqDataInfo);
        return probableBuDatas;
    }

    public BuDataInfo getTargetBuData(String optionNumStr, List<BuDataInfo> targetBuDataInfos){
        int optionNum = Integer.parseInt(optionNumStr);
        if(targetBuDataInfos != null && targetBuDataInfos.size() > optionNum){
            BuDataInfo buDataInfo = targetBuDataInfos.get(optionNum - 1);
            buDataInfo.setBelief(1.0f);
            return buDataInfo;
        }
        return null;
    }
}
