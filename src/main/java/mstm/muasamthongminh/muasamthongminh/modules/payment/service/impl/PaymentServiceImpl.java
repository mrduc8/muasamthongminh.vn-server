package mstm.muasamthongminh.muasamthongminh.modules.payment.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.address.model.Address;
import mstm.muasamthongminh.muasamthongminh.modules.address.repository.AddressRepository;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.Cart;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.CartItem;
import mstm.muasamthongminh.muasamthongminh.modules.cart.repository.CartRepository;
import mstm.muasamthongminh.muasamthongminh.modules.cart.service.impl.CartServiceImpl;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.CheckoutRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.CheckoutResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.ShippingStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.mapper.OrderMapper;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.OrderItem;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.ShippingHistory;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Shippings;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.OrderItemRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.OrderRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.ShippingHistoryRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.ShippingRepository;
import mstm.muasamthongminh.muasamthongminh.modules.payment.service.PaymentService;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.repository.ProductVariantsRepository;
import mstm.muasamthongminh.muasamthongminh.modules.user.repository.UserRepository;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepo;
    private final AddressRepository addressRepo;
    private final UserRepository userRepo;
    private final CartRepository cartRepo;
    private final OrderItemRepository orderItemRepo;
    private final ShippingRepository shippingRepo;
    private final ShippingHistoryRepository historyRepo;
    private final ProductVariantsRepository variantRepo;
    private final CartServiceImpl cartService;

    @Override
    public CheckoutResponse checkout(CheckoutRequest req, Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepo.findById(req.getAddressId()).orElseThrow(() -> new RuntimeException("Address not found"));
        Cart cart = cartRepo.findTopByUserIdAndStatusOrderByUpdatedAtDesc(userId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem ci : cart.getItems()) {
            ProductVariants variant = variantRepo.findById(ci.getProductVariantId().longValue())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            if (ci.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng không hợp lệ: " + ci.getNameSnapshot());
            }

            if (ci.getQuantity() > variant.getStockQuantity()) {
                throw new RuntimeException("Sản phẩm " + ci.getNameSnapshot() +
                        " không đủ hàng. Còn lại: " + variant.getStockQuantity());
            }

            variant.setStockQuantity(variant.getStockQuantity() - ci.getQuantity());
            variantRepo.save(variant);
        }

        Orders order = Orders.builder()
                .userId(user)
                .addressId(address)
                .paymentMethod(req.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.CREATED)
                .subtotal(cart.getSubtotal())
                .shippingFee(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .grandTotal(cart.getGrandTotal())
                .build();

        order = orderRepo.save(order);

        for (CartItem ci : cart.getItems()) {
            ProductVariants variant = variantRepo.findById(ci.getProductVariantId().longValue())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            OrderItem oi = OrderItem.builder()
                    .orderId(order)
                    .productId(variant.getProductId())
                    .productVariantId(variant)
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice())
                    .lineTotal(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                    .nameSnapshot(variant.getProductId().getName())
                    .imageUrlSnapshot(variant.getProductId().getMainImageUrl())
                    .variantLabelSnapshot(ci.getVariantLabelSnapshot())
                    .build();
            orderItemRepo.save(oi);
        }

        cartService.closeCart(cart);

        Cart newCart = Cart.builder()
                .userId(user.getId())
                .status("ACTIVE")
                .currency("VND")
                .build();
        cartRepo.save(newCart);

        Shippings shipping = Shippings.builder()
                .orderId(order)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingFee(BigDecimal.ZERO)
                .build();
        shipping = shippingRepo.save(shipping);

        ShippingHistory history = ShippingHistory.builder()
                .shippingId(shipping)
                .status(ShippingStatus.PENDING)
                .note("Đơn hàng vừa được tạo")
                .build();
        historyRepo.save(history);
        String redirectUrl = null;

        switch (req.getPaymentMethod()) {
            case COD -> {
                order.setPaymentStatus(PaymentStatus.PENDING);
                order.setOrderStatus(OrderStatus.CREATED);
                orderRepo.save(order);
            }
            case MOMO -> redirectUrl = callMomoApi(order);
            case VNPAY -> redirectUrl = callVnPayApi(order);
            case PAYPAL -> redirectUrl = callPaypalApi(order);
        }

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .grandTotal(order.getGrandTotal())
                .redirectUrl(redirectUrl)
                .build();
    }

    private String callMomoApi(Orders order) {
        // TODO: tích hợp MoMo
        return "https://momo.vn/pay/" + order.getId();
    }

    private String callVnPayApi(Orders order) {
        // TODO: tích hợp VNPAY
        return "https://sandbox.vnpayment.vn/checkout/" + order.getId();
    }

    private String callPaypalApi(Orders order) {
        // TODO: tích hợp PayPal
        return "https://paypal.com/checkout?orderId=" + order.getId();
    }
}
