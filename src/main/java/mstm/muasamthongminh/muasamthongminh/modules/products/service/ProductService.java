package mstm.muasamthongminh.muasamthongminh.modules.products.service;

import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductRequest;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public interface ProductService {
    ResponseEntity<?> createProduc(ProductRequest request, Long userId);

    List<ProductResponse> getProductsByShopId(Long shopId);

    ResponseEntity<?> updateProduct(Long productId, ProductRequest request, Long userId);

    ResponseEntity<?> deleteProduct(Long productId, Long userId);

    List<ProductResponse> searchProductsVi(String keyword);

    List<ProductResponse> getAllPublicProducts();

    List<ProductResponse> getPublicProductsByCategory(Long categoryId);

    List<ProductResponse> getPublicProductsByCategoryIncludeAll(Long categoryId);

    ResponseEntity<ProductResponse> getPublicProductDetail(Long productId);
}
