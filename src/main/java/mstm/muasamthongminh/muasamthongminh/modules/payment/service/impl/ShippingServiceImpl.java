package mstm.muasamthongminh.muasamthongminh.modules.payment.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.service.MailService;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.ShippingStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.mapper.OrderMapper;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Shippings;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.OrderRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.ShippingRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.service.ShippingService;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private  final ShippingRepository shippingRepository;
    private final MailService mailService;

    @Override
    public OrderResponse confirmDelivered(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại"));

        Shippings shipping = order.getShipping();
        if (shipping == null) {
            throw new RuntimeException("Đơn hàng chưa có thông tin vận chuyển");
        }

        // Cập nhật trạng thái vận chuyển
        shipping.setShippingStatus(ShippingStatus.DELIVERED);
        shipping.setUpdatedAt(java.time.LocalDateTime.now());
        shippingRepository.save(shipping);

        // Cập nhật trạng thái đơn hàng
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(java.time.LocalDateTime.now());

        // Nếu chưa thanh toán thì đánh dấu đã thanh toán
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        orderRepository.save(order);

        // Gửi email thông báo cho khách hàng
        if (order.getUserId() != null && order.getUserId().getEmail() != null) {
            try {
                String content = """
                Xin chào %s,
                
                Đơn hàng #%d của bạn đã được giao thành công.
                
                Tổng tiền: %s đ
                Cảm ơn bạn đã mua sắm cùng chúng tôi!
                """.formatted(
                        order.getUserId().getName() != null
                                ? order.getUserId().getName()
                                : order.getUserId().getUsername(),
                        order.getId(),
                        order.getGrandTotal()
                );

                mailService.sendSimpleMail(
                        order.getUserId().getEmail(),
                        "Đơn hàng #" + order.getId() + " đã giao thành công",
                        content
                );
            } catch (Exception ex) {
                System.err.println("❌ Lỗi gửi mail cho " + order.getUserId().getEmail() + ": " + ex.getMessage());
            }
        }

        return OrderMapper.toResponse(order);
    }
}
