package com.cooler.ai.distribution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NLUIntent {
    private Integer id;

    private String nluIntentName;

    private String nluDomainName;

    private Integer nluIntentType;

    private Integer intentId;

    private String version;

    private Byte enable;

    private String msg;

}