package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record HisseFiyatEntry(
        @JsonProperty("record_id")   String recordId,
        @JsonProperty("asset_code")  String assetCode,
        @JsonProperty("data_date")   String dataDate,
        @JsonProperty("record_date") String recordDate,
        @JsonProperty("close_price") BigDecimal closePrice,
        @JsonProperty("high_price")  BigDecimal highPrice,
        @JsonProperty("low_price")   BigDecimal lowPrice,
        @JsonProperty("open_price")  BigDecimal openPrice
) {}
