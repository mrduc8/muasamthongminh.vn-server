package mstm.muasamthongminh.muasamthongminh.modules.payment.repository;

import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Shippings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shippings, Long> {
}
