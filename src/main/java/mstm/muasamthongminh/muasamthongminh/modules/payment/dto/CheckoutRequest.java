package mstm.muasamthongminh.muasamthongminh.modules.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.JoinColumn;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentMethod;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest
{
    private Long addressId;
    private PaymentMethod paymentMethod;
}
