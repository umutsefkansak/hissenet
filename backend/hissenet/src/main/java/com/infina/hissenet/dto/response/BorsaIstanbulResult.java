package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BorsaIstanbulResult(
        BigDecimal current,
        BigDecimal changerate,
        BigDecimal opening,
        BigDecimal closing,
        BigDecimal min,
        BigDecimal max,
        String time,
        String date
) {}
