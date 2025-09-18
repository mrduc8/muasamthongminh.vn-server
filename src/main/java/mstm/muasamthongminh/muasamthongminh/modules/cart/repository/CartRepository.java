package mstm.muasamthongminh.muasamthongminh.modules.cart.repository;

import mstm.muasamthongminh.muasamthongminh.modules.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // Lấy giỏ hàng ACTIVE theo userId
// Trả về danh sách thay vì Optional
    List<Cart> findByUserIdAndStatus(Long userId, String status);

    // hoặc chỉ lấy cái mới nhất
    Optional<Cart> findTopByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, String status);

    List<Cart> findByUserId(Long userId);

    // Lấy giỏ hàng ACTIVE theo sessionId (guest)
    Optional<Cart> findBySessionIdAndStatus(String sessionId, String status);
}
