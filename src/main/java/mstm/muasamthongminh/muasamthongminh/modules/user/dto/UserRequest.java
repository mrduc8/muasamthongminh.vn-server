package mstm.muasamthongminh.muasamthongminh.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.address.model.Address;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserRequest {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String sex;
    private String birthday;
}
