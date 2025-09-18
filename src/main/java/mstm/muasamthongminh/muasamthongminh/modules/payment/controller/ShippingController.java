package mstm.muasamthongminh.muasamthongminh.modules.payment.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.service.impl.ShippingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shippings")
@RequiredArgsConstructor
public class ShippingController {
    private final ShippingServiceImpl shippingService;

    @PutMapping("/{orderId}/delivered")
    public ResponseEntity<OrderResponse> confirmDelivered(@PathVariable Long orderId) {
        OrderResponse response = shippingService.confirmDelivered(orderId);
        return ResponseEntity.ok(response);
    }
}
