package com.cooler.ai.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NLUIntent {
    private Integer id;

    private String nluIntentName;

    private String nluDomainName;

    private Integer nluIntentType;

    private Integer intentId;

    private Byte enable;

    private String msg;

}