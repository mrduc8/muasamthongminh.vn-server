package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.repository;

import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepsitory extends JpaRepository<Bank, Long> {
    // kiểm tra dữ liệu của user
    boolean existsByUserId(Long userId);

    // Xoá dữ liệu của user
    void deleteByUserId(Long userId);

    List<Bank> findByUserId(Long userId);
    List<Bank> findByShopId(Long shopId);
}
