package mstm.muasamthongminh.muasamthongminh.modules.cart.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.mapper.CartMapper;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.Cart;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.CartItem;
import mstm.muasamthongminh.muasamthongminh.modules.cart.repository.CartItemRepository;
import mstm.muasamthongminh.muasamthongminh.modules.cart.repository.CartRepository;
import mstm.muasamthongminh.muasamthongminh.modules.cart.service.CartService;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;
import mstm.muasamthongminh.muasamthongminh.modules.products.repository.ProductVariantsRepository;
import mstm.muasamthongminh.muasamthongminh.modules.products.repository.ProductsRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductVariantsRepository productVariantsRepository;
    private final ProductsRepository productsRepository;
    private final ShopRepository shopRepository;

    private CartDto buildCartDto (Cart cart, List<CartItem> items){
        List<Long> shopIds = items.stream()
                .map(CartItem::getShopId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, Shop> shopMap = shopRepository.findAllById(shopIds).stream()
                .collect(Collectors.toMap(Shop::getId, s -> s, (s1, s2) -> s1));

        return CartMapper.toDto(cart, items, shopMap);
    }

    @Override
    @Transactional
    public CartDto getCart(Long userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        // nạp items để mapper trả ra luôn
        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        return buildCartDto(cart, items);
    }

    @Override
    public CartDto addItem(Long userId, String sessionId, Integer variantId, int quantity) {
        if (variantId == null || quantity <= 0) {
            throw new IllegalArgumentException("variantId bắt buộc và quantity phải > 0");
        }

        Cart cart = getOrCreateCart(userId, sessionId);

        ProductVariants variant = productVariantsRepository.findById(variantId.longValue())
                .orElseThrow(() -> new RuntimeException("Biến thể không tồn tại: " + variantId));

        Products product = variant.getProductId(); // theo entity của bạn

        BigDecimal unitPrice = resolveUnitPrice(variant);

        // (tuỳ chọn) build nhãn phân loại từ bảng PVAV nếu muốn
        String variantLabel = buildVariantLabel(variant.getId().intValue());

        CartItem item = cartItemRepository.findByCart_IdAndProductVariantId(cart.getId(), variantId).orElse(null);
        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .shopId(product.getShopId() != null ? product.getShopId().getId() : null)
                    .productId(product.getId())
                    .productVariantId(variantId)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .nameSnapshot(product.getName())
                    .imageUrlSnapshot(product.getMainImageUrl())
                    .variantLabelSnapshot(variantLabel)
                    .build();
            cartItemRepository.save(item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
            // policy: cập nhật theo giá hiện tại
            item.setUnitPrice(unitPrice);
        }

        recalcTotals(cart);
        cart = cartRepository.save(cart);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        return buildCartDto(cart, items);
    }

    @Override
    public CartDto updateQuantity(Long userId, String sessionId, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(userId, sessionId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item không thuộc giỏ hiện tại");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }

        recalcTotals(cart);
        cart = cartRepository.save(cart);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        return buildCartDto(cart, items);
    }

    @Override
    public CartDto removeItem(Long userId, String sessionId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId, sessionId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item không thuộc giỏ hiện tại");
        }

        cartItemRepository.delete(item);

        recalcTotals(cart);
        cart = cartRepository.save(cart);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        return buildCartDto(cart, items);
    }

    @Override
    public CartDto mergeGuestToUser(String sessionId, Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId bắt buộc");

        Cart guest = cartRepository.findBySessionIdAndStatus(sessionId, "ACTIVE").orElse(null);
        Cart userCart = getOrCreateCart(userId, null);

        if (guest != null) {
            List<CartItem> guestItems = cartItemRepository.findByCart_Id(guest.getId());
            for (CartItem gi : guestItems) {
                addItem(userId, null, gi.getProductVariantId(), gi.getQuantity());
            }
            guest.setStatus("ABANDONED");
            cartRepository.save(guest);
        }
        return getCart(userId, null);
    }

    private Cart getOrCreateCart(Long userId, String sessionId) {
        if (userId != null) {
            // Lấy bản ghi mới nhất
            Cart latest = cartRepository
                    .findTopByUserIdAndStatusOrderByUpdatedAtDesc(userId, "ACTIVE")
                    .orElse(null);

            if (latest != null) {
                // (tuỳ chọn) Dọn rác: nếu còn cart ACTIVE khác => chuyển ABANDONED
                List<Cart> dups = cartRepository.findByUserIdAndStatus(userId, "ACTIVE");
                for (Cart c : dups) {
                    if (!c.getId().equals(latest.getId())) {
                        c.setStatus("ABANDONED");
                        cartRepository.save(c);
                    }
                }
                return latest;
            }

            // Không có thì tạo mới
            return cartRepository.save(Cart.builder()
                    .userId(userId)
                    .currency("VND")
                    .status("ACTIVE")
                    .build());
        }

        // guest…
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Thiếu sessionId cho guest");
        }
        return cartRepository.findBySessionIdAndStatus(sessionId, "ACTIVE")
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .sessionId(sessionId)
                        .currency("VND")
                        .status("ACTIVE")
                        .build()));
    }

    /** Ưu tiên dùng salePrice nếu > 0, fallback originalPrice */
    private BigDecimal resolveUnitPrice(ProductVariants variant) {
        if (variant.getSalePrice() != null && variant.getSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            return variant.getSalePrice();
        }
        if (variant.getOriginalPrice() != null) {
            return variant.getOriginalPrice();
        }
        return BigDecimal.ZERO;
    }

    /** Tính lại tổng tiền của giỏ */
    private void recalcTotals(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem i : items) {
            BigDecimal line = i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
            subtotal = subtotal.add(line);
        }
        cart.setSubtotal(subtotal);
        cart.setDiscountTotal(BigDecimal.ZERO);
        cart.setShippingTotal(BigDecimal.ZERO);
        cart.setTaxTotal(BigDecimal.ZERO);
        cart.setGrandTotal(
                subtotal
                        .subtract(cart.getDiscountTotal())
                        .add(cart.getShippingTotal())
                        .add(cart.getTaxTotal())
        );
    }

    /** (Tuỳ chọn) Build nhãn phân loại từ PVAV nếu cần hiển thị, tạm trả null */
    private String buildVariantLabel(Integer variantId) {
        // Gợi ý: join product_variant_attribute_values -> attribute_vales để ghép "Tên - Giá trị"
        return null;
    }

    public void closeCart(Cart cart) {
        // Đổi trạng thái giỏ
        cart.setStatus("CHECKED_OUT");
        cartRepository.save(cart);

        // Nếu muốn clear luôn items để giỏ trống
        cartItemRepository.deleteAll(cart.getItems());
    }

}
