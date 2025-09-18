package mstm.muasamthongminh.muasamthongminh.modules.cart.repository;

import mstm.muasamthongminh.muasamthongminh.modules.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndProductVariantId(Long cartId, Integer productVariantId);

    // Lấy tất cả items trong cart
    List<CartItem> findByCart_Id(Long cartId);
}
