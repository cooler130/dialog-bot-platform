package com.cooler.ai.platform.facade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainInfo implements Serializable {
    private String requestID;                   // 请求ID

    private String domainName;                  // 领域
    private String intentName;                  // 意图
    private double score;                       // 结果得分
    private Map<Integer, SlotInfo> slots;       // 填槽结果
    private String utterance;                   // 原文
    private String uttSegment;                  // 原文分词

    private String uttPos;                      // 原文词性

    private int errCode = 0;                    // 错误代码(TODO)
    private String errMsg = "";

}
