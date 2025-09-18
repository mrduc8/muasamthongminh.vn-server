package mstm.muasamthongminh.muasamthongminh.modules.reports.repository;

import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Products, Long> {

    // 1. Doanh thu theo ngày cho 1 shop
    @Query("SELECT DATE(o.createdAt), SUM(o.grandTotal) " +
            "FROM Orders o " +
            "JOIN o.orderItems i " +
            "JOIN i.productId p " +
            "WHERE o.orderStatus = 'COMPLETED' " +
            "AND p.shopId.id = :shopId " +
            "GROUP BY DATE(o.createdAt)")
    List<Object[]> getRevenueByDayForShop(@Param("shopId") Long shopId);

    // 2. Sản phẩm tồn kho sắp hết theo shop
    @Query("SELECT p.id, p.name, SUM(v.stockQuantity) " +
            "FROM Products p JOIN p.variants v " +
            "WHERE p.shopId.id = :shopId " +
            "GROUP BY p.id, p.name " +
            "HAVING SUM(v.stockQuantity) < :threshold")
    List<Object[]> getLowStockProductsForShop(@Param("shopId") Long shopId,
                                              @Param("threshold") int threshold);

    // 3. Top sản phẩm bán chạy theo shop
    @Query("SELECT i.productId.id, i.nameSnapshot, SUM(i.quantity) as totalSold " +
            "FROM OrderItem i JOIN i.orderId o " +
            "JOIN i.productId p " +
            "WHERE o.orderStatus = 'COMPLETED' " +
            "AND p.shopId.id = :shopId " +
            "GROUP BY i.productId.id, i.nameSnapshot " +
            "ORDER BY totalSold DESC")
    List<Object[]> getTopSellingProductsForShop(@Param("shopId") Long shopId,
                                                Pageable pageable);
}


