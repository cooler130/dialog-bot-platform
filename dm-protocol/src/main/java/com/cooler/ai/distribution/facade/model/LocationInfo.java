package com.cooler.ai.distribution.facade.model;

import lombok.Data;

@Data
public class LocationInfo implements java.io.Serializable{
    private Integer latitude;
    private Integer longitude;
    private String cityName;
}
