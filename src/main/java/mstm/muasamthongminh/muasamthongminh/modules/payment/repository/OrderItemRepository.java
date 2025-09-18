package mstm.muasamthongminh.muasamthongminh.modules.payment.repository;

import mstm.muasamthongminh.muasamthongminh.modules.payment.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>
{
}
