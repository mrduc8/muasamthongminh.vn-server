package mstm.muasamthongminh.muasamthongminh.modules.payment.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.ShippingRequests;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Shippings;

public class ShippingMapper {
    public static Shippings toEntity(ShippingRequests req, Orders od){
        if (req == null) return null;
        if (od == null) return null;

        return Shippings.builder()
                .id(req.getId())
                .orderId(od)
                .shippingStatus(req.getShippingStatus())
                .shippingFee(req.getShippingFee())
                .trackingNumber(req.getTrackingNumber())
                .createdAt(req.getCreatedAt())
                .updatedAt(req.getUpdatedAt())
                .build();
    }

    public static  ShippingRequests toDto(Shippings sp) {
        if (sp == null) return null;

        ShippingRequests req = new ShippingRequests();
        req.setId(sp.getId());
        req.setOrderId(sp.getOrderId() == null ? null : sp.getOrderId().getId());
        req.setShippingStatus(sp.getShippingStatus());
        req.setShippingFee(sp.getShippingFee());
        req.setTrackingNumber(sp.getTrackingNumber());
        req.setCreatedAt(sp.getCreatedAt());
        req.setUpdatedAt(sp.getUpdatedAt());
        return req;
    }
}
