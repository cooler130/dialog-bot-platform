package com.cooler.ai.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Slot {
    private Integer id;

    private String slotName;

    private Integer intentId;

    private Byte isNecessary;

    private Float importanceDegree;

    private Byte inheritable;

    private Byte inheritDestructible;

    private String followedSlots;

    private String defaultQuery;

    private String msg;

}