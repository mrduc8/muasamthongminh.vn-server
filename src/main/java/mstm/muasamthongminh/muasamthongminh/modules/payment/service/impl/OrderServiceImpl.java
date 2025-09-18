package mstm.muasamthongminh.muasamthongminh.modules.payment.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.service.MailService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.mapper.OrderMapper;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.OrderRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.service.OrderService;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final MailService mailService;

    @Override
    public List<OrderResponse> getOrdersForLoggedInShop(Long userId) {
        Shop shop = shopRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User chưa có shop"));
        List<Orders> orders = orderRepository.findOrdersByShopId(shop.getId());
        return orders.stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse confirmOrder(Long orderId, Long userId) {
        // Lấy shop theo user
        Shop shop = shopRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User chưa có shop"));

        // Lấy order
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại"));

        // Check order có thuộc shop này không
        boolean hasProductInShop = order.getOrderItems().stream()
                .anyMatch(oi -> oi.getProductId().getShopId().getId().equals(shop.getId()));

        if (!hasProductInShop) {
            throw new RuntimeException("Đơn hàng không thuộc shop của bạn");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order);

        // Gửi email cho user
        User u = order.getUserId();
        if (u != null && u.getEmail() != null && !u.getEmail().isBlank()) {
            try {
                String displayName = u.getName() != null && !u.getName().isBlank()
                        ? u.getName()
                        : u.getUsername();

                String content = """
                Xin chào %s,
                
                Đơn hàng #%d của bạn đã được shop xác nhận và đang được xử lý.
                
                Tổng tiền: %s đ
                Trạng thái hiện tại: đang giao hàng đến bạn.
                
                Cảm ơn bạn đã mua sắm cùng chúng tôi!
                """.formatted(
                        displayName,
                        order.getId(),
                        order.getGrandTotal()
                );

                mailService.sendSimpleMail(
                        u.getEmail(),
                        "Đơn hàng #" + order.getId() + " đã được xác nhận",
                        content
                );
            } catch (Exception ex) {
                // log warning, không throw tránh rollback
                System.err.println("❌ Lỗi gửi mail cho " + u.getEmail() + ": " + ex.getMessage());
            }
        }

        return OrderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderMapper::toResponse)
                .toList();
    }
}
