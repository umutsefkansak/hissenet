package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.infina.hissenet.dto.request.StockData;

import java.util.List;

public record StockApiResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("result")  List<StockData> result
) {}
