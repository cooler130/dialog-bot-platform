package com.cooler.ai.platform.action;

import java.util.Map;

/**
 * TaskAction，就是对请求数据、对话状态数据、业务数据进行处理的动作过程
 * @param <DR>
 * @param <DS>
 * @param <BD>
 */
public interface TaskAction<DR, DS, BD> {                                  //DR，DmRequest请求数据；DS，DialogState对话状态数据；BD，BizData业务数据

    /**
     * 顶层接口，设置请求数据、对话状态数据、业务数据
     * @param dmRequest
     * @param dialogState
     * @param bizDataMSMap
     */
    void setData(DR dmRequest, DS dialogState, Map<String, BD> bizDataMSMap);

}
