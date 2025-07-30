package mstm.muasamthongminh.muasamthongminh.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.enums.Role;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private List<String> roles;
    private String message;
}
