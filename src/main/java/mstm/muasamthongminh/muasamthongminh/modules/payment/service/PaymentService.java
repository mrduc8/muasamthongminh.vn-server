package mstm.muasamthongminh.muasamthongminh.modules.payment.service;

import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.CheckoutRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.CheckoutResponse;

public interface PaymentService {
    CheckoutResponse checkout(CheckoutRequest req, Long userId);
}
