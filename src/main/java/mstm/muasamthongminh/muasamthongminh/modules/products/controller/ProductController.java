package mstm.muasamthongminh.muasamthongminh.modules.products.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductRequest;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.products.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@ModelAttribute ProductRequest productRequest, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        return productService.createProduc(productRequest, userId);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductRequest productRequest,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return productService.updateProduct(productId, productRequest, userId);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>> getProductsByShop(@PathVariable Long shopId) {
        List<ProductResponse> products = productService.getProductsByShopId(shopId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return productService.deleteProduct(productId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProductsVi(@RequestParam("q") String q) {
        List<ProductResponse> result = productService.searchProductsVi(q);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/public/all")
    public ResponseEntity<List<ProductResponse>> getAllPublicProducts() {
        return ResponseEntity.ok(productService.getAllPublicProducts());
    }

    @GetMapping("/public/{productId}")
    public ResponseEntity<ProductResponse> getPublicProductDetail(
            @PathVariable Long productId
    ) {
        return productService.getPublicProductDetail(productId);
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getPublicProductsByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(productService.getPublicProductsByCategory(categoryId));
    }

    @GetMapping("/public/category/{categoryId}/all-status")
    public ResponseEntity<List<ProductResponse>> getPublicProductsByCategoryIncludeAll(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(productService.getPublicProductsByCategoryIncludeAll(categoryId));
    }

}
