package mstm.muasamthongminh.muasamthongminh.modules.payment.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController
{
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        List<OrderResponse> orders = orderService.getOrdersForLoggedInShop(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        OrderResponse updatedOrder = orderService.confirmOrder(orderId, userId);
        return ResponseEntity.ok(updatedOrder);
    }
}
