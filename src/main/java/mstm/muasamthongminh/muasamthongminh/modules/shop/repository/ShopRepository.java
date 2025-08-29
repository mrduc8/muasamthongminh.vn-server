package mstm.muasamthongminh.muasamthongminh.modules.shop.repository;

import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // Kiểm tra user
    boolean existsByUserId(Long userId);

    Optional<Shop> findByUserId(Long userId);

    // Đồng bộ trạng thái với shop_requests
    Optional<Shop> findByShopRequestsId(Long shopRequestsId);

    // Xoá dữ liệu user
    void deleteByUserId(Long userId);

    List<Shop> findByShopRequestsIdIn(List<Long> requestIds);

    // Lấy shop theo userId
    Optional<Shop> findByUser_Id(Long userId);
}
