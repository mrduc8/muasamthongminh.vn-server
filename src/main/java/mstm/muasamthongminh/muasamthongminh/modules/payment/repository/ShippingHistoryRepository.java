package mstm.muasamthongminh.muasamthongminh.modules.payment.repository;

import mstm.muasamthongminh.muasamthongminh.modules.payment.model.ShippingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingHistoryRepository extends JpaRepository<ShippingHistory, Long> {
}
