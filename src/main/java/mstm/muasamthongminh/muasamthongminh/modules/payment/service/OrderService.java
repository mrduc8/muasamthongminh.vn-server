package mstm.muasamthongminh.muasamthongminh.modules.payment.service;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrdersForLoggedInShop(Long userId);

    OrderResponse confirmOrder(Long orderId, Long userId);

    List<OrderResponse> getAllOrders();

}
