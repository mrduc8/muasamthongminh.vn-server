package mstm.muasamthongminh.muasamthongminh.modules.payment.repository;

import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o " +
            "JOIN o.orderItems i " +
            "WHERE o.orderStatus = :orderStatus " +
            "AND o.paymentStatus = :paymentStatus " +
            "AND i.productId.shopId.id = :shopId")
    List<Orders> findCompletedAndPaidOrdersByShop(
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("shopId") Long shopId
    );


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
