package com.cooler.ai.platform.facade.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class BaseModelState<T> implements ModelState<T>, Serializable {

    private Integer modelId = null;                                     //结构ID

    private String modelName = null;                                    //结构名称

    private T t = null;                                                 //未知数据类型
}
