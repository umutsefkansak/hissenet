package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockData(
        @JsonProperty("rate")        BigDecimal rate,
        @JsonProperty("lastprice")   BigDecimal lastprice,
        @JsonProperty("lastpricestr")String lastpricestr,
        @JsonProperty("hacim")       BigDecimal hacim,
        @JsonProperty("hacimstr")    String hacimstr,
        @JsonProperty("min")         BigDecimal min,
        @JsonProperty("minstr")      String minstr,
        @JsonProperty("max")         BigDecimal max,
        @JsonProperty("maxstr")      String maxstr,
        @JsonProperty("time")        String time,
        @JsonProperty("text")        String text,
        @JsonProperty("code")        String code,
        @JsonProperty("icon")        String icon
) {
    @JsonCreator
    public StockData { }
}