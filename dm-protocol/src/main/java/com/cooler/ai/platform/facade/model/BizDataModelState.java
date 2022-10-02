package com.cooler.ai.platform.facade.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BizDataModelState<T> implements ModelState<T>, Serializable {
    /**
     * 说明：
     * 这个业务数据是在对话sessionId下第turnNum轮产生的；                                （这两个量是对人能理解的定位的量，代表第sessionId次人机对话的第几轮对话）
     * 其被认定为{domainName + taskName} 话题下第 domainTaskTurnNum 轮的业务数据；         （这两个量是对机器能理解的定位量，代表这个数据是认定为domainName+taskId话题下第domainTaskTurnNum轮对话产生的业务数据）
     * 在botName这个bot里面得到；                                                     （这个是用来定位物理bot的量，代表是哪个bot产生了这个业务数据）
     * 数据名称为 bizItemName ，值为t，此数据在话题范畴能持续 keepDomainTaskTurnCount轮；  （这3个量是这个业务数据的本体，分别为名称、值、作用轮次）
     */

    private String sessionId = null;
    private int turnNum = -1;

    private String domainName = null;
    private String taskName = null;
    private int domainTaskTurnNum = -1;

    private String botName = null;

    private String bizItemName = null;                                              //业务项名称
    private T t = null;                                                             //业务项值
    private int keepDomainTaskTurnCount = 0;                                        //特意保留的轮次（默认不保留/不记忆）

}
