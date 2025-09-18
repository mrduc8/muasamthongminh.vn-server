package mstm.muasamthongminh.muasamthongminh.modules.cart.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.AddItemDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.UpdateQtyDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.service.CartService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication, HttpServletRequest request) {
        Long userId = extractUserId(authentication);
        String sessionId = (userId != null) ? null : extractGuestSession(request);
        return ResponseEntity.ok(cartService.getCart(userId, sessionId));
    }

    /** Thêm item vào giỏ */
    @PostMapping(value = "/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDto> addItem(@RequestBody AddItemDto dto,
                                           Authentication authentication,
                                           HttpServletRequest request) {
        Long userId = extractUserId(authentication);
        String sessionId = (userId != null) ? null : extractGuestSession(request);
        CartDto cart = cartService.addItem(userId, sessionId, dto.getVariantId(), dto.getQuantity());
        return ResponseEntity.ok(cart);
    }

    /** Cập nhật số lượng (quantity <= 0 thì xóa dòng) */
    @PatchMapping(value = "/items/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDto> updateQty(@PathVariable Long itemId,
                                             @RequestBody UpdateQtyDto dto,
                                             Authentication authentication,
                                             HttpServletRequest request) {
        Long userId = extractUserId(authentication);
        String sessionId = (userId != null) ? null : extractGuestSession(request);
        CartDto cart = cartService.updateQuantity(userId, sessionId, itemId, dto.getQuantity());
        return ResponseEntity.ok(cart);
    }

    /** Xóa một dòng item khỏi giỏ */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable Long itemId,
                                              Authentication authentication,
                                              HttpServletRequest request) {
        Long userId = extractUserId(authentication);
        String sessionId = (userId != null) ? null : extractGuestSession(request);
        CartDto cart = cartService.removeItem(userId, sessionId, itemId);
        return ResponseEntity.ok(cart);
    }

    /** (Tuỳ chọn) Merge giỏ guest -> user ngay sau khi đăng nhập */
    @PostMapping("/merge")
    public ResponseEntity<CartDto> mergeGuest(Authentication authentication, HttpServletRequest request) {
        Long userId = extractUserId(authentication);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        String sessionId = extractGuestSession(request);
        CartDto cart = cartService.mergeGuestToUser(sessionId, userId);
        return ResponseEntity.ok(cart);
    }

    // ----------------- Helpers -----------------

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails cud)) return null;
        User user = cud.getUser();
        return (user != null) ? user.getId() : null;
    }

    private String extractGuestSession(HttpServletRequest request) {
        // FE nên gửi header này cho guest. Nếu thiếu thì tự phát sinh tạm (khuyên FE lưu cookie để giữ ổn định).
        String sid = request.getHeader("X-Guest-Session");
        if (sid == null || sid.isBlank()) {
            sid = "guest_" + java.util.UUID.randomUUID();
        }
        return sid;
    }
}
