package com.infina.hissenet.dto.response;

import java.util.Map;

public record HisseResult(HisseData data, Map<String, Object> summary) {}

