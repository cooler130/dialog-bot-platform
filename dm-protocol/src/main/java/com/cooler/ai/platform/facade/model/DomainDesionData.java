package com.cooler.ai.platform.facade.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DomainDesionData {

    private String sessionId = null;
    private int turnNum = -1;

    private String sentence = null;
    private String nluDomainName = null;
    private String nluIntentName = null;
    private String domainName = null;
    private String intentName = null;
    private Integer intentId = null;
    private String taskName = null;
    private Integer lastFromStateId = null;
    private String lastFromState = null;
    private Integer fromStateId = null;                                                                                 //此轮的fromStateId一开始为lastToStateId，后面可能会变。
    private String fromState = null;
    private Boolean sameDomain = false;

    private Map<String, SlotState> historySlotStateMap = null;                                                          //装载Language分发数据体
    private Map<String, SlotState> currentSlotStateMap = null;
    private Map<String, String> exchangedRecordMap = null;
    private Map<String, SlotState> fixedSlotStateMap = null;
    private Map<String, SlotState> unknownSlotStateMap = null;

    private Map<String, String> fixedParamValueMap = null;                                                               //直接装载nonLanguage分发数据体

}
