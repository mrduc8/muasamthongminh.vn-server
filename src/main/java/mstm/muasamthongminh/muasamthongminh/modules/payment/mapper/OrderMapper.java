package mstm.muasamthongminh.muasamthongminh.modules.payment.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.address.model.Address;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderItemRequests;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderRequest;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderResponse;
import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.ShippingRequests;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentMethod;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;

import java.util.stream.Collectors;

public class OrderMapper {
    public static Orders toEntity(OrderRequest orderRequest, User user, Address address) {
        if (orderRequest == null) return null;
        if (user == null) return null;
        if (address == null) return null;

        return Orders.builder()
                .userId(user)
                .addressId(address)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.CREATED)
                .subtotal(orderRequest.getSubtotal())
                .shippingFee(orderRequest.getShippingFee())
                .discount(orderRequest.getDiscount())
                .grandTotal(orderRequest.getGrandTotal())
                .createdAt(orderRequest.getCreatedAt())
                .updatedAt(orderRequest.getUpdatedAt())
                .build();
    }

    public static OrderRequest toRequest(Orders orders) {
        if (orders == null) return null;

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setId(orders.getId());
        orderRequest.setUserId(orders.getUserId() != null ? orders.getUserId().getId() : null);
        orderRequest.setAddressId(orders.getAddressId() != null ? orders.getAddressId().getId() : null);
        orderRequest.setPaymentMethod(orders.getPaymentMethod());
        orderRequest.setPaymentStatus(orders.getPaymentStatus());
        orderRequest.setOrderStatus(orders.getOrderStatus());
        orderRequest.setSubtotal(orders.getSubtotal());
        orderRequest.setShippingFee(orders.getShippingFee());
        orderRequest.setDiscount(orders.getDiscount());
        orderRequest.setGrandTotal(orders.getGrandTotal());
        orderRequest.setCreatedAt(orders.getCreatedAt());
        orderRequest.setUpdatedAt(orders.getUpdatedAt());

        return orderRequest;
    }


    public static OrderResponse toResponse(Orders order) {
        if (order == null) return null;

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId() != null ? order.getUserId().getId() : null)
                .addressId(order.getAddressId() != null ? order.getAddressId().getId() : null)
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discount(order.getDiscount())
                .grandTotal(order.getGrandTotal())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getOrderItems() != null
                        ? order.getOrderItems().stream().map(oi -> {
                    OrderItemRequests dto = new OrderItemRequests();
                    dto.setId(oi.getId());
                    dto.setOrderId(order.getId());
                    dto.setProductId(oi.getProductId() != null ? oi.getProductId().getId() : null);
                    dto.setProductVariantId(oi.getProductVariantId() != null ? oi.getProductVariantId().getId() : null);
                    dto.setQuantity(oi.getQuantity());
                    dto.setUnitPrice(oi.getUnitPrice());
                    dto.setLineTotal(oi.getLineTotal());
                    dto.setNameSnapshot(oi.getNameSnapshot());
                    dto.setImageUrlSnapshot(oi.getImageUrlSnapshot());
                    dto.setVariantLabelSnapshot(oi.getVariantLabelSnapshot());
                    return dto;
                }).collect(Collectors.toList())
                        : null)
                .shipping(order.getShipping() != null ? mapShipping(order) : null)
                .build();
    }

    private static ShippingRequests mapShipping(Orders order) {
        ShippingRequests dto = new ShippingRequests();
        dto.setId(order.getShipping().getId());
        dto.setOrderId(order.getId());
        dto.setShippingStatus(order.getShipping().getShippingStatus());
        dto.setShippingFee(order.getShipping().getShippingFee());
        dto.setTrackingNumber(order.getShipping().getTrackingNumber());
        dto.setCreatedAt(order.getShipping().getCreatedAt());
        dto.setUpdatedAt(order.getShipping().getUpdatedAt());
        return dto;
    }


}
