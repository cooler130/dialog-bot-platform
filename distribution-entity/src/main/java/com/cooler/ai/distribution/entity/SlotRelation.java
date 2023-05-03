package com.cooler.ai.distribution.entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlotRelation {
    private Integer id;

    private Integer nluSlotId;

    private Integer slotId;

    private Integer enable;

    private String msg;
}