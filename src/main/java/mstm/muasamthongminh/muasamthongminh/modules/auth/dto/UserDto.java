package mstm.muasamthongminh.muasamthongminh.modules.auth.dto;

import lombok.*;
import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.common.enums.Status;
import mstm.muasamthongminh.muasamthongminh.modules.address.model.Address;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String sex;
    private String birthday;
    private List<Address> addresses;
    private Status status;
    private List<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
