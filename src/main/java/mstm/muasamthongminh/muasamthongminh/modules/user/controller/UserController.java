package mstm.muasamthongminh.muasamthongminh.modules.user.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.config.JwtUtils;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.UserDto;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.user.dto.UserRequest;
import mstm.muasamthongminh.muasamthongminh.modules.user.mapper.UserMapper;
import mstm.muasamthongminh.muasamthongminh.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private final UserService userService;
    @Autowired
    private AuthUserRepository authUserRepository;

    @GetMapping
    public List<UserDto> getUser(){
        return userService.getUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = authUserRepository.findById(id).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PostMapping("/{id}/update-role")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId,
                                        @RequestBody Map<String, String> request) {
        // Parse string từ client thành enum Role
        Role roleEnum = Role.valueOf(request.get("role").toUpperCase());

        // Gọi service để cập nhật
        return userService.updateRoleUser(userId, roleEnum);
    }

    @PutMapping(value = "/{id}/update-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserDetails(
            @PathVariable("id") Long userId,
            @RequestPart("userDto") UserDto userDto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        return userService.updateDetailsUser(userId, userDto, avatar);
    }

    // Cập nhập user
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@CookieValue(value = "token", required = false) String token, @RequestParam String name, @RequestParam String phone, @RequestParam(required = false) String sex,
                                        @RequestParam(required = false) String birthday, @RequestPart(required = false) MultipartFile avatar) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Chưa đăng nhập"));
        }

        try {
            String email = jwtUtils.extractUsername(token);
            UserRequest updatedUser = userService.updateUser(email, name, phone, avatar, sex, birthday);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cập nhật thất bại", "error", e.getMessage()));
        }
    }

    // Tìm kiếm user
    @GetMapping("/search")
    public List<UserRequest> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }

    // Xoá user
    @DeleteMapping("/{id}/delete-user")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }
}
