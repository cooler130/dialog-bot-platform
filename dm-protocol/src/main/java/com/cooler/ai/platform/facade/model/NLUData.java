package com.cooler.ai.platform.facade.model;

import lombok.Data;
import java.util.List;

@Data
public class NLUData implements java.io.Serializable{
    private List<DomainInfo> result;
    private String oldFormatResult;                      // 用来兼容历史格式版本，之后会删除
}
