package mstm.muasamthongminh.muasamthongminh.modules.payment.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.payment.dto.OrderItemRequests;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.OrderItem;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;

public class OrderItemMapper {
    public static OrderItem toEntity(OrderItemRequests req, Orders orders, Products products, ProductVariants variants) {
        if (req == null) return null;
        if (products == null) return null;
        if (variants == null) return null;
        if (orders == null) return null;

        return OrderItem.builder()
                .id(req.getId())
                .orderId(orders)
                .productId(products)
                .productVariantId(variants)
                .quantity(req.getQuantity())
                .unitPrice(req.getUnitPrice())
                .lineTotal(req.getLineTotal())
                .nameSnapshot(req.getNameSnapshot())
                .imageUrlSnapshot(req.getImageUrlSnapshot())
                .variantLabelSnapshot(req.getVariantLabelSnapshot())
                .build();
    }

    public static OrderItemRequests toDto(OrderItem ot) {
        if (ot == null) return null;

        OrderItemRequests req = new OrderItemRequests();
        req.setOrderId(ot.getId());
        req.setOrderId(ot.getOrderId() == null ? null : ot.getOrderId().getId());
        req.setProductId(ot.getProductId() == null ? null : ot.getProductId().getId());
        req.setProductVariantId(ot.getProductVariantId() == null ? null : ot.getProductVariantId().getId());
        req.setQuantity(ot.getQuantity());
        req.setUnitPrice(ot.getUnitPrice());
        req.setLineTotal(ot.getLineTotal());
        req.setNameSnapshot(ot.getNameSnapshot());
        req.setImageUrlSnapshot(ot.getImageUrlSnapshot());
        req.setVariantLabelSnapshot(ot.getVariantLabelSnapshot());
        return req;
    }
}
