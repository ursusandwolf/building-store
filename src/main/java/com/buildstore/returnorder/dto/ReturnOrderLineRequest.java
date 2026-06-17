package com.buildstore.returnorder.dto;

import java.math.BigDecimal;

public record ReturnOrderLineRequest(Long salesOrderLineId, BigDecimal quantity, String reason) {}
