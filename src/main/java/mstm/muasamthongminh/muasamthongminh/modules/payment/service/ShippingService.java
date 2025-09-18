package mstm.muasamthongminh.muasamthongminh.modules.payment.service;

import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;

public interface ShippingService {
    OrderResponse confirmDelivered(Long orderId);
}
