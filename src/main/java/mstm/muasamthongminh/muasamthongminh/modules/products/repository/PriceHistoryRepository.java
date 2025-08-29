package mstm.muasamthongminh.muasamthongminh.modules.products.repository;

import mstm.muasamthongminh.muasamthongminh.modules.products.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
}
