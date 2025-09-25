package mstm.muasamthongminh.muasamthongminh.modules.payment.service.impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
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
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import mstm.muasamthongminh.muasamthongminh.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ShopRepository shopRepository;
    private final APIContext apiContext;

    @Override
    public CheckoutResponse checkout(CheckoutRequest req, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        Address address = addressRepo.findById(req.getAddressId())
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại!"));

        Cart cart = cartRepo.findTopByUserIdAndStatusOrderByUpdatedAtDesc(userId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại!"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        // Kiểm tra tồn kho
        for (CartItem ci : cart.getItems()) {
            ProductVariants variant = variantRepo.findById(ci.getProductVariantId().longValue())
                    .orElseThrow(() -> new RuntimeException("Biến thể không tồn tại"));
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

        // Nhóm item theo shop
        Map<Long, List<CartItem>> groupedByShop = cart.getItems().stream()
                .collect(Collectors.groupingBy(CartItem::getShopId));

        List<Orders> createdOrders = new ArrayList<>();

        for (Map.Entry<Long, List<CartItem>> entry : groupedByShop.entrySet()) {
            Long shopId = entry.getKey();
            List<CartItem> shopItems = entry.getValue();

            BigDecimal subtotal = shopItems.stream()
                    .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new RuntimeException("Shop không tồn tại"));

            Orders order = Orders.builder()
                    .userId(user)
                    .shopId(shop) // gắn shop cho order
                    .addressId(address)
                    .paymentMethod(req.getPaymentMethod())
                    .paymentStatus(PaymentStatus.PENDING)
                    .orderStatus(OrderStatus.CREATED)
                    .subtotal(subtotal)
                    .shippingFee(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .grandTotal(subtotal)
                    .build();

            order = orderRepo.save(order);

            for (CartItem ci : shopItems) {
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

            createdOrders.add(order);
        }

        // Đóng giỏ hàng
        cartService.closeCart(cart);

        // Tạo cart mới
        Cart newCart = Cart.builder()
                .userId(user.getId())
                .status("ACTIVE")
                .currency("VND")
                .build();
        cartRepo.save(newCart);

        // Tổng tiền để thanh toán (toàn bộ đơn)
        BigDecimal totalAmount = createdOrders.stream()
                .map(Orders::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String redirectUrl = null;
        switch (req.getPaymentMethod()) {
            case COD -> {
                createdOrders.forEach(o -> {
                    o.setPaymentStatus(PaymentStatus.PENDING);
                    o.setOrderStatus(OrderStatus.CREATED);
                    orderRepo.save(o);
                });
            }
            case MOMO -> redirectUrl = callMomoApi(createdOrders);
            case VNPAY -> redirectUrl = callVnPayApi(createdOrders);
            case PAYPAL -> redirectUrl = callPaypalApi(createdOrders, totalAmount);
        }

        return CheckoutResponse.builder()
                .orderIds(createdOrders.stream().map(Orders::getId).toList()) // list orderId
                .paymentMethod(req.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.CREATED)
                .grandTotal(totalAmount)
                .redirectUrl(redirectUrl)
                .build();
    }

    // Với MoMo, VNPay, bạn có thể gửi 1 request cho toàn bộ giỏ
    private String callMomoApi(List<Orders> orders) {
        return "https://momo.vn/pay/" + orders.get(0).getId();
    }

    private String callVnPayApi(List<Orders> orders) {
        return "https://sandbox.vnpayment.vn/checkout/" + orders.get(0).getId();
    }

    private String callPaypalApi(List<Orders> orders, BigDecimal totalAmount) {
        BigDecimal exchangeRate = new BigDecimal("26155");
        BigDecimal amountUsd = totalAmount.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(amountUsd.toString());

        Transaction transaction = new Transaction();
        transaction.setDescription("Thanh toán đơn hàng #" + orders.stream().map(Orders::getId).toList());
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8080/api/payments/paypal/cancel");
        redirectUrls.setReturnUrl("http://localhost:8080/api/payments/paypal/success");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));
        payment.setRedirectUrls(redirectUrls);

        try {
            Payment createdPayment = payment.create(apiContext);
            for (Links link : createdPayment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Lỗi tạo PayPal Payment: " + e.getMessage());
        }
        return null;
    }
}
