package mstm.muasamthongminh.muasamthongminh.modules.auth.security;

import lombok.AllArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    // Trả về danh sách quyền của người dùng
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName().name())
                .toList();
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // mật khẩu từ database
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // email là username
    }

    // Những phần dưới có thể hardcode true hoặc theo trạng thái user
    @Override
    public boolean isAccountNonExpired() {
        return true; // có thể kiểm tra user.getExpiredAt()
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ví dụ user.getStatus() != "LOCKED"
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // có thể kiểm tra thời gian đổi mật khẩu
    }

    @Override
    public boolean isEnabled() {
        return true; // có thể kiểm tra user.getStatus().equals("ACTIVE")
    }

    // Lấy User gốc nếu cần dùng trong controller
    public User getUser() {
        return user;
    }
}
