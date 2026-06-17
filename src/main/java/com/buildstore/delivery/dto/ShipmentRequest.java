package com.buildstore.delivery.dto;

import com.buildstore.delivery.model.DeliveryType;
import java.util.List;

public record ShipmentRequest(Long orderId,
                              Long warehouseId,
                              DeliveryType deliveryType,
                              String deliveryAddress,
                              List<ShipmentLineRequest> lines) {
}
