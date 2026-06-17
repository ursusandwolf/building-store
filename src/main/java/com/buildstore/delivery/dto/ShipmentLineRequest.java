package com.buildstore.delivery.dto;

import java.math.BigDecimal;

public record ShipmentLineRequest(Long orderLineId,
                                  BigDecimal quantity,
                                  String unit) {
}
