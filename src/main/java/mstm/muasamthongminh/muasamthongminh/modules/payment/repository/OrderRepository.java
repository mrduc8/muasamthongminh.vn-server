package mstm.muasamthongminh.muasamthongminh.modules.payment.repository;

import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("""
        SELECT DISTINCT o 
        FROM Orders o
        JOIN OrderItem oi ON oi.orderId = o
        JOIN Products p ON oi.productId = p
        WHERE p.shopId.id = :shopId
        ORDER BY o.createdAt DESC
    """)
    List<Orders> findOrdersByShopId(Long shopId);
}
