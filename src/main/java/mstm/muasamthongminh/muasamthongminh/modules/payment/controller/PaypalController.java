package mstm.muasamthongminh.muasamthongminh.modules.payment.controller;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/paypal")
@RequiredArgsConstructor
public class PaypalController {
    private final APIContext apiContext;
    private final OrderRepository orderRepo;

    @GetMapping("/success")
    public String success(@RequestParam("paymentId") String paymentId,
                          @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution exec = new PaymentExecution();
            exec.setPayerId(payerId);

            Payment executedPayment = payment.execute(apiContext, exec);

            String desc = executedPayment.getTransactions().get(0).getDescription();
            Long orderId = Long.valueOf(desc.replace("Thanh toán đơn hàng #", ""));

            Orders order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.COMPLETED);
            orderRepo.save(order);

            return "Thanh toán thành công cho đơn hàng #" + order.getId();
        } catch (Exception e) {
            return "Lỗi khi xác nhận thanh toán: " + e.getMessage();
        }
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Thanh toán bị hủy.";
    }
}
