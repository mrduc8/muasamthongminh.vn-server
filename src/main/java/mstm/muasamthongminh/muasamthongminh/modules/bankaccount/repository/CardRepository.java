package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.repository;

import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    // kiểm tra dữ liệu của user
    boolean existsByUserId(Long userId);
}
