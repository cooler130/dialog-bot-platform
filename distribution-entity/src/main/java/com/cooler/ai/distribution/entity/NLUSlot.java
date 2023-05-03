package com.cooler.ai.distribution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NLUSlot {
    private Integer id;

    private String nluSlotName;

    private String nluDomainName;

    private Integer nluSlotType;

    private Integer enable;

    private String msg;

}