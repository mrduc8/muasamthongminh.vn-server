package mstm.muasamthongminh.muasamthongminh.modules.products.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.service.ImageUploadService;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import mstm.muasamthongminh.muasamthongminh.modules.brands.repository.BrandRepository;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import mstm.muasamthongminh.muasamthongminh.modules.categories.repository.CategoriesRepository;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductRequest;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.AttributeStatus;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductStatus;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductVariantStatus;
import mstm.muasamthongminh.muasamthongminh.modules.products.mapper.ProductMapper;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.*;
import mstm.muasamthongminh.muasamthongminh.modules.products.repository.*;
import mstm.muasamthongminh.muasamthongminh.modules.products.service.ProductService;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImplService implements ProductService {

    @Autowired
    private final ShopRepository shopRepository;
    @Autowired
    private final BrandRepository brandRepository;
    @Autowired
    private final CategoriesRepository categoriesRepository;
    @Autowired
    private final ProductsRepository productsRepository;
    @Autowired
    private final ProductImagesRepository productImagesRepository;
    @Autowired
    private final PriceHistoryRepository priceHistoryRepository;
    @Autowired
    private final ProductVariantsRepository productVariantsRepository;
    @Autowired
    private final ProductVariantAttributeValuesRepository productVariantAttributeValuesRepository;
    @Autowired
    private final AttributeValuesRepository attributeValuesRepository;
    @Autowired
    private final AttributesRepository attributesRepository;
    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public ResponseEntity<?> createProduc(ProductRequest request, Long userId) {
        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa đăng ký shop!"));

        Categories category = categoriesRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Ngành hàng không tồn tại!"));

        Brands brands = null;
        if (request.getBrandId() != null) {
            brands = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
        }


        // Upload ảnh chính (nếu có)
        if (request.getMainImage() != null && !request.getMainImage().isEmpty()) {
            try {
                String mainImage = imageUploadService.uploadImage(request.getMainImage());
                request.setMainImageUrl(mainImage);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh chính sản phẩm: " + e.getMessage());
            }
        }

        // Map -> save Products
        ProductDto dto = new ProductDto();
        dto.setName(request.getName());
        dto.setBrandId(request.getBrandId());
        dto.setCategoryId(request.getCategoryId());
        dto.setShopId(shop.getId());
        dto.setDescription(request.getDescription());
        dto.setMainImageUrl(request.getMainImageUrl());
        dto.setMetaTitle(request.getMetaTitle());
        dto.setMetaDescription(request.getMetaDescription());
        dto.setStatus(ProductStatus.ACTIVE);

        Products product = ProductMapper.toEntity(dto, shop, brands, category);
        product = productsRepository.save(product);

        // Lưu nhiều ảnh + chuẩn bị list ảnh cho response
        List<ProductRequest.ImageRequest> imagesForResponse = new ArrayList<>();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImages> toSave = new ArrayList<>();

            for (ProductRequest.ImageRequest imageReq : request.getImages()) {
                String url = null;

                if (imageReq.getImage() != null && !imageReq.getImage().isEmpty()) {
                    try {
                        url = imageUploadService.uploadImage(imageReq.getImage());
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi upload ảnh phụ: " + e.getMessage());
                    }
                } else if (imageReq.getImageUrl() != null && !imageReq.getImageUrl().isBlank()) {
                    url = imageReq.getImageUrl();
                }

                if (url == null) {
                    throw new RuntimeException("Ảnh phụ phải có file upload hoặc imageUrl!");
                }

                ProductImages img = new ProductImages();
                img.setProductId(product);
                img.setImageUrl(url);
                img.setAltText(imageReq.getAltText());
                img.setSortOrder(imageReq.getSortOrder());
                img.setCreatedAt(LocalDateTime.now());
                toSave.add(img);

                // Response object
                ProductRequest.ImageRequest respImg = new ProductRequest.ImageRequest();
                respImg.setImageUrl(url);
                respImg.setImage(null);
                respImg.setAltText(imageReq.getAltText());
                respImg.setSortOrder(imageReq.getSortOrder());
                imagesForResponse.add(respImg);
            }
            productImagesRepository.saveAll(toSave);
        }

        // Lưu variants + attributes và build response variants
        List<ProductRequest.VariantRequest> variantsForResponse = new ArrayList<>();
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (ProductRequest.VariantRequest vReq : request.getVariants()) {
                ProductVariants variant = new ProductVariants();
                variant.setProductId(product);
                variant.setSku(vReq.getSku());
                variant.setOriginalPrice(vReq.getOriginalPrice());
                variant.setSalePrice(vReq.getSalePrice());
                variant.setStockQuantity(vReq.getStockQuantity());
                variant.setStatus(ProductVariantStatus.ACTIVE);
                variant.setCreatedAt(LocalDateTime.now());
                variant = productVariantsRepository.save(variant);

                // Attributes (upload ảnh nếu có + tạo attribute/attribute_value nếu thiếu)
                List<ProductRequest.AttributeRequest> attrsForResponse = new ArrayList<>();
                if (vReq.getAttributes() != null && !vReq.getAttributes().isEmpty()) {
                    for (ProductRequest.AttributeRequest aReq : vReq.getAttributes()) {
                        String attrImgUrl = aReq.getImageUrl();
                        if (aReq.getImage() != null && !aReq.getImage().isEmpty()) {
                            try {
                                attrImgUrl = imageUploadService.uploadImage(aReq.getImage());
                            } catch (IOException e) {
                                throw new RuntimeException("Lỗi upload ảnh thuộc tính: " + e.getMessage());
                            }
                        }

                        // Tìm/khởi tạo Attributes theo slug
                        Attributes attr = attributesRepository.findBySlug(aReq.getSlug())
                                .orElse(null);

                        if (attr == null) {
                            attr = Attributes.builder()
                                    .name(aReq.getName())
                                    .slug(aReq.getSlug())
                                    .status(AttributeStatus.TEXT)
                                    .product(product)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            attr = attributesRepository.save(attr);
                        }

                        // Lưu AttributeValues (không link variant trong thiết kế hiện tại)
                        AttributeValues attrVal = AttributeValues.builder()
                                .attributeId(attr)
                                .value(aReq.getValue())
                                .slug(aReq.getSlug())
                                .product(product)
                                .variant(variant)
                                .colorCode(aReq.getColorCode())
                                .imageUrl(attrImgUrl)
                                .createdAt(LocalDateTime.now())
                                .build();
                        attributeValuesRepository.save(attrVal);

                        //Map dữ liệu
                        ProductVariantAttributeValues pvav = ProductVariantAttributeValues.builder()
                                .productVariant(variant)
                                .attributeValue(attrVal)
                                .id(new ProductVariantAttributeValues.PK(
                                        variant.getId(),
                                        attrVal.getId()
                                ))
                                .build();
                        productVariantAttributeValuesRepository.save(pvav);

                        // Bản ghi trả về theo ProductRequest.AttributeRequest
                        ProductRequest.AttributeRequest respAttr = new ProductRequest.AttributeRequest();
                        respAttr.setName(aReq.getName());
                        respAttr.setValue(aReq.getValue());
                        respAttr.setSlug(aReq.getSlug());
                        respAttr.setColorCode(aReq.getColorCode());
                        respAttr.setImageUrl(attrImgUrl);
                        respAttr.setImage(null);
                        attrsForResponse.add(respAttr);
                    }
                }

                // Gom Variant cho response
                ProductRequest.VariantRequest respVar = new ProductRequest.VariantRequest();
                respVar.setSku(vReq.getSku());
                respVar.setOriginalPrice(vReq.getOriginalPrice());
                respVar.setSalePrice(vReq.getSalePrice());
                respVar.setStockQuantity(vReq.getStockQuantity());
                respVar.setAttributes(attrsForResponse);
                variantsForResponse.add(respVar);
            }
        }

        // Build đối tượng trả về theo đúng schema ProductRequest
        ProductRequest response = new ProductRequest();
        response.setName(product.getName());
        response.setCategoryId(category.getId());
        response.setBrandId(brands != null ? brands.getId() : null);
        response.setShopId(shop.getId());
        response.setDescription(product.getDescription());
        response.setMainImageUrl(product.getMainImageUrl());
        response.setMetaTitle(product.getMetaTitle());
        response.setMetaDescription(product.getMetaDescription());
        response.setImages(imagesForResponse);
        response.setVariants(variantsForResponse);

        return ResponseEntity.ok(response);
    }

    @Override
    public List<ProductResponse> getProductsByShopId(Long shopId) {
        List<Products> products = productsRepository.findByShopId_Id(shopId);

        return products.stream().map(p -> {
            List<ProductImages> imgs = productImagesRepository.findByProductId_Id(p.getId());
            List<ProductVariants> vars = productVariantsRepository.findByProductId_Id(p.getId());
            List<AttributeValues> attrVals = attributeValuesRepository.findByProduct_Id(p.getId());

            return ProductMapper.toResponse(p, imgs, vars, attrVals);
        }).collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> updateProduct(Long productId, ProductRequest request, Long userId) {

        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa đăng ký shop!"));

        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
        if (product.getShopId() == null || !product.getShopId().getId().equals(shop.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật sản phẩm này!");
        }

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getMetaTitle() != null) product.setMetaTitle(request.getMetaTitle());
        if (request.getMetaDescription() != null) product.setMetaDescription(request.getMetaDescription());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        if (request.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Ngành hàng không tồn tại!"));
            product.setCategoryId(category);
        }

        if (request.getBrandId() != null) {
            Brands brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại!"));
            product.setBrandId(brand);
        }

        if (request.getMainImage() != null && !request.getMainImage().isEmpty()) {
            try {
                String mainImage = imageUploadService.uploadImage(request.getMainImage());
                product.setMainImageUrl(mainImage);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh chính: " + e.getMessage());
            }
        } else if (request.getMainImageUrl() != null && !request.getMainImageUrl().isBlank()) {
            product.setMainImageUrl(request.getMainImageUrl());
        }

        product.setUpdatedAt(LocalDateTime.now());
        product = productsRepository.save(product);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImages> current = productImagesRepository.findByProductId_Id(product.getId());
            java.util.Map<Long, ProductImages> byOrder = new java.util.HashMap<>();
            for (ProductImages img : current) {
                Long key = (img.getSortOrder() == null) ? -1L : img.getSortOrder();
                byOrder.put(key, img);
            }

            // 2) Duyệt các ảnh gửi lên
            for (ProductRequest.ImageRequest imageReq : request.getImages()) {
                Long orderKey = (imageReq.getSortOrder() == null) ? -1L : imageReq.getSortOrder();
                ProductImages target = byOrder.get(orderKey);

                // Tìm URL mới (nếu có)
                String url = null;

                // 2.1 Ưu tiên URL text nếu có
                if (imageReq.getImageUrl() != null && !imageReq.getImageUrl().isBlank()) {
                    url = imageReq.getImageUrl().trim();
                }

                // 2.2 Nếu không có URL mà có file thì upload
                if (url == null && imageReq.getImage() != null && !imageReq.getImage().isEmpty()) {
                    try {
                        url = imageUploadService.uploadImage(imageReq.getImage());
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi upload ảnh phụ: " + e.getMessage());
                    }
                }

                // 2.3 Nếu không có URL lẫn file:
                // - Nếu ảnh đã tồn tại (target != null) => giữ nguyên URL cũ (cho phép update altText/sortOrder)
                // - Nếu ảnh chưa tồn tại => đang 'thêm mới' nhưng thiếu dữ liệu ảnh -> lỗi như create
                if (url == null) {
                    if (target == null) {
                        throw new RuntimeException("Ảnh phụ phải có file upload hoặc imageUrl khi thêm mới!");
                    } else {
                        url = target.getImageUrl(); // giữ nguyên khi chỉ sửa altText/metadata
                    }
                }

                // 3) Lưu/ cập nhật
                if (target == null) {
                    target = new ProductImages();
                    target.setProductId(product);
                    target.setCreatedAt(LocalDateTime.now());
                }

                target.setImageUrl(url);
                target.setAltText(imageReq.getAltText());
                target.setSortOrder(imageReq.getSortOrder());
                target.setUpdatedAt(LocalDateTime.now());

                productImagesRepository.save(target);
            }
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            List<ProductVariants> existing = productVariantsRepository.findByProductId_Id(product.getId());
            java.util.Map<String, ProductVariants> skuToVariant = new java.util.HashMap<>();
            for (ProductVariants v : existing) {
                if (v.getSku() != null) skuToVariant.put(v.getSku(), v);
            }

            for (ProductRequest.VariantRequest vReq : request.getVariants()) {
                if (vReq.getSku() == null || vReq.getSku().isBlank()) {
                    throw new RuntimeException("SKU của biến thể không được để trống!");
                }
                ProductVariants variant = skuToVariant.get(vReq.getSku());
                boolean isNew = false;
                if (variant == null) {
                    variant = new ProductVariants();
                    variant.setProductId(product);
                    variant.setSku(vReq.getSku());
                    variant.setStatus(ProductVariantStatus.ACTIVE);
                    variant.setCreatedAt(LocalDateTime.now());
                    isNew = true;
                }

                if (vReq.getOriginalPrice() != null) variant.setOriginalPrice(vReq.getOriginalPrice());
                if (vReq.getSalePrice() != null) variant.setSalePrice(vReq.getSalePrice());
                if (vReq.getStockQuantity() != null) variant.setStockQuantity(vReq.getStockQuantity());

                variant.setUpdatedAt(LocalDateTime.now());
                variant = productVariantsRepository.save(variant);

                if (vReq.getAttributes() != null && !vReq.getAttributes().isEmpty()) {
                    for (ProductRequest.AttributeRequest aReq : vReq.getAttributes()) {
                        String attrImgUrl = aReq.getImageUrl();
                        if (aReq.getImage() != null && !aReq.getImage().isEmpty()) {
                            try {
                                attrImgUrl = imageUploadService.uploadImage(aReq.getImage());
                            } catch (IOException e) {
                                throw new RuntimeException("Lỗi upload ảnh thuộc tính: " + e.getMessage());
                            }
                        }

                        Attributes attr = attributesRepository.findBySlug(aReq.getSlug()).orElse(null);
                        if (attr == null) {
                            attr = Attributes.builder()
                                    .name(aReq.getName())
                                    .slug(aReq.getSlug())
                                    .status(AttributeStatus.TEXT)
                                    .product(product)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            attr = attributesRepository.save(attr);
                        }

                        AttributeValues attrVal = AttributeValues.builder()
                                .attributeId(attr)
                                .value(aReq.getValue())
                                .slug(aReq.getSlug())
                                .product(product)
                                .variant(variant)
                                .colorCode(aReq.getColorCode())
                                .imageUrl(attrImgUrl)
                                .createdAt(LocalDateTime.now())
                                .build();
                        attrVal = attributeValuesRepository.save(attrVal);

                        ProductVariantAttributeValues pvav = ProductVariantAttributeValues.builder()
                                .productVariant(variant)
                                .attributeValue(attrVal)
                                .id(new ProductVariantAttributeValues.PK(
                                        variant.getId(),
                                        attrVal.getId()
                                ))
                                .build();
                        productVariantAttributeValuesRepository.save(pvav);
                    }
                }
            }
        }

        List<ProductImages> imgs = productImagesRepository.findByProductId_Id(product.getId());
        List<ProductRequest.ImageRequest> imagesForResponse = new ArrayList<>();
        if (imgs != null) {
            for (ProductImages img : imgs) {
                ProductRequest.ImageRequest respImg = new ProductRequest.ImageRequest();
                respImg.setImageUrl(img.getImageUrl());
                respImg.setAltText(img.getAltText());
                respImg.setSortOrder(img.getSortOrder());
                imagesForResponse.add(respImg);
            }
        }

        List<ProductRequest.VariantRequest> variantsForResponse = new ArrayList<>();
        List<ProductVariants> varsNow = productVariantsRepository.findByProductId_Id(product.getId());
        if (varsNow != null) {
            for (ProductVariants v : varsNow) {
                ProductRequest.VariantRequest respVar = new ProductRequest.VariantRequest();
                respVar.setSku(v.getSku());
                respVar.setOriginalPrice(v.getOriginalPrice());
                respVar.setSalePrice(v.getSalePrice());
                respVar.setStockQuantity(v.getStockQuantity());
                variantsForResponse.add(respVar);
            }
        }

        ProductRequest response = new ProductRequest();
        response.setName(product.getName());
        response.setCategoryId(product.getCategoryId() != null ? product.getCategoryId().getId() : null);
        response.setBrandId(product.getBrandId() != null ? product.getBrandId().getId() : null);
        response.setShopId(shop.getId());
        response.setDescription(product.getDescription());
        response.setMainImageUrl(product.getMainImageUrl());
        response.setMetaTitle(product.getMetaTitle());
        response.setMetaDescription(product.getMetaDescription());
        response.setImages(imagesForResponse);
        response.setVariants(variantsForResponse);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteProduct(Long productId, Long userId) {
        // 1) Xác thực shop
        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng chưa đăng ký shop!"));

        // 2) Tìm sản phẩm
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        if (product.getShopId() == null || !product.getShopId().getId().equals(shop.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa sản phẩm này!");
        }

        // 3) Xóa ảnh phụ
        List<ProductImages> images = productImagesRepository.findByProductId_Id(product.getId());
        if (images != null && !images.isEmpty()) {
            productImagesRepository.deleteAll(images);
        }

        // 4) Xóa variants + attribute values
        List<ProductVariants> variants = productVariantsRepository.findByProductId_Id(product.getId());
        if (variants != null && !variants.isEmpty()) {
            for (ProductVariants variant : variants) {
                // Xóa attribute values liên kết với variant
                List<AttributeValues> attrVals = attributeValuesRepository.findByVariant_Id(variant.getId());
                if (attrVals != null && !attrVals.isEmpty()) {
                    // Xóa link ProductVariantAttributeValues
                    for (AttributeValues av : attrVals) {
                        productVariantAttributeValuesRepository.deleteByProductVariantIdAndAttributeValueId(
                                variant.getId().intValue(), av.getId()
                        );
                    }
                    attributeValuesRepository.deleteAll(attrVals);
                }
            }
            productVariantsRepository.deleteAll(variants);
        }

        // 5) Xóa attributes gắn với product
        List<Attributes> attrs = attributesRepository.findByProduct_Id(product.getId());
        if (attrs != null && !attrs.isEmpty()) {
            attributesRepository.deleteAll(attrs);
        }

        // 6) Cuối cùng xóa product
        productsRepository.delete(product);

        return ResponseEntity.ok("Xóa sản phẩm thành công!");
    }

    @Override
    public List<ProductResponse> searchProductsVi(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim();
        List<Products> products = productsRepository.searchVi(kw);

        return products.stream().map(p -> {
            List<ProductImages> imgs = productImagesRepository.findByProductId_Id(p.getId());
            List<ProductVariants> vars = productVariantsRepository.findByProductId_Id(p.getId());
            List<AttributeValues> attrVals = attributeValuesRepository.findByProduct_Id(p.getId());
            return ProductMapper.toResponse(p, imgs, vars, attrVals);
        }).toList();
    }

    @Override
    public List<ProductResponse> getAllPublicProducts() {
        List<Products> products = productsRepository.findByStatus(ProductStatus.ACTIVE);

        return products.stream().map(p -> {
            List<ProductImages> imgs = productImagesRepository.findByProductId_Id(p.getId());
            List<ProductVariants> vars = productVariantsRepository.findByProductId_Id(p.getId());
            List<AttributeValues> attrVals = attributeValuesRepository.findByProduct_Id(p.getId());
            return ProductMapper.toResponse(p, imgs, vars, attrVals);
        }).toList();
    }

    @Override
    public List<ProductResponse> getPublicProductsByCategory(Long categoryId) {
        categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ngành hàng không tồn tại!"));

        List<Products> products = productsRepository
                .findByCategoryId_IdAndStatusOrderByCreatedAtDesc(categoryId, ProductStatus.ACTIVE);

        return products.stream().map(p -> {
            List<ProductImages> imgs = productImagesRepository.findByProductId_Id(p.getId());
            List<ProductVariants> vars = productVariantsRepository.findByProductId_Id(p.getId());
            List<AttributeValues> attrVals = attributeValuesRepository.findByProduct_Id(p.getId());
            return ProductMapper.toResponse(p, imgs, vars, attrVals);
        }).toList();
    }

    @Override
    public List<ProductResponse> getPublicProductsByCategoryIncludeAll(Long categoryId) {
        categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ngành hàng không tồn tại!"));

        List<Products> products = productsRepository
                .findByCategoryId_IdOrderByCreatedAtDesc(categoryId);

        return products.stream().map(p -> {
            List<ProductImages> imgs = productImagesRepository.findByProductId_Id(p.getId());
            List<ProductVariants> vars = productVariantsRepository.findByProductId_Id(p.getId());
            List<AttributeValues> attrVals = attributeValuesRepository.findByProduct_Id(p.getId());
            return ProductMapper.toResponse(p, imgs, vars, attrVals);
        }).toList();
    }

    @Override
    public ResponseEntity<ProductResponse> getPublicProductDetail(Long productId) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new RuntimeException("Sản phẩm hiện không khả dụng!");
        }

        List<ProductImages> imgs = productImagesRepository.findByProductId_Id(product.getId());

        List<ProductVariants> allVariants = productVariantsRepository.findByProductId_Id(product.getId());
        List<ProductVariants> activeVariants = (allVariants == null) ? List.of()
                : allVariants.stream()
                .filter(v -> v.getStatus() == null || v.getStatus() == ProductVariantStatus.ACTIVE)
                .toList();

        List<AttributeValues> attrVals = attributeValuesRepository.findByProduct_Id(product.getId());

        ProductResponse response = ProductMapper.toResponse(product, imgs, activeVariants, attrVals);

        return ResponseEntity.ok(response);
    }

}
