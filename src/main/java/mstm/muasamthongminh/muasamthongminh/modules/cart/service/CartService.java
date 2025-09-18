package mstm.muasamthongminh.muasamthongminh.modules.cart.service;

import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartDto;

public interface CartService {

    /** Lấy giỏ (tự tạo nếu chưa có) theo userId hoặc sessionId (guest) */
    CartDto getCart(Long userId, String sessionId);

    /** Thêm 1 variant vào giỏ (merge số lượng nếu đã tồn tại) */
    CartDto addItem(Long userId, String sessionId, Integer variantId, int quantity);

    /** Cập nhật số lượng 1 dòng item; quantity <= 0 thì xóa dòng */
    CartDto updateQuantity(Long userId, String sessionId, Long cartItemId, int quantity);

    /** Xóa 1 dòng item */
    CartDto removeItem(Long userId, String sessionId, Long cartItemId);

    /** Hợp nhất giỏ guest vào giỏ user khi đăng nhập */
    CartDto mergeGuestToUser(String sessionId, Long userId);
}
