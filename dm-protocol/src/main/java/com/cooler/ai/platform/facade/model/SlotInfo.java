package com.cooler.ai.platform.facade.model;

import lombok.Data;

@Data
public class SlotInfo implements java.io.Serializable{
    private int index;                                  // 槽索引
    private String name;                                // 槽名
    private String value;                               // 槽值
    private int valueType = 1;                          // 槽值类型
    private int slotOpe = 1;                            // 槽值操作
    private String originalText;                        // 原文文本
}
