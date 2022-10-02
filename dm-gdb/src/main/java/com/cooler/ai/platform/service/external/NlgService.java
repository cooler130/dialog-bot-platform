package com.cooler.ai.platform.service.external;

import com.cooler.ai.platform.facade.model.Message;

import java.util.List;
import java.util.Map;


/**
 * Created by fengkang on 2018/8/1.
 */
public interface NlgService<T> {

    /**
     * NLG文件的初始化工作
     */
    void init();

    /**
     *NLG模块负责根据DM产生的对外输出动作生成相应的话术。
     *目前以话术模版形式实现对不同场景的对话回复生成。
     *@param bizDatas
     *@return Message结构为DM与US接口定义中的Message
     */
    List<Message> nlgProcess(List<T> bizDatas, Map<String, String> payLoadParamMap);

}
