package mstm.muasamthongminh.muasamthongminh.modules.payment.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.CheckoutRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.CheckoutResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        CheckoutResponse response = paymentService.checkout(request, userId);
        return ResponseEntity.ok(response);
    }
}
