package com.buildstore.returnorder.dto;

import java.util.List;

public record ReturnOrderRequest(Long salesOrderId, List<ReturnOrderLineRequest> lines) {}
