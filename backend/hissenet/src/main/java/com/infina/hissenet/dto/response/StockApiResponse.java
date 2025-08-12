package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StockApiResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("result")  List<StockData> result
) {}
