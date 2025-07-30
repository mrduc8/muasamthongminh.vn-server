    package mstm.muasamthongminh.muasamthongminh.modules.auth.controller;

    import jakarta.servlet.http.HttpServletResponse;
    import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
    import mstm.muasamthongminh.muasamthongminh.common.enums.Status;
    import mstm.muasamthongminh.muasamthongminh.config.JwtUtils;
    import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.*;
    import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
    import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
    import mstm.muasamthongminh.muasamthongminh.modules.auth.service.AuthService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.ResponseCookie;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {
        @Autowired private AuthService authService;
        @Autowired private JwtUtils jwtUtils;
        @Autowired private AuthUserRepository authUserRepository;

        private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Khai báo logger

        @GetMapping("/me")
        public ResponseEntity<?> getUserInfo(@CookieValue(value = "token", required = false) String token) {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Token không tồn tại"));
            }

            try {
                // Ví dụ gọi đến authService để lấy thông tin user từ token
                var userInfo = authService.getUserInfoFromToken(token);
                return ResponseEntity.ok(userInfo);
            } catch (Exception e) {
                return ResponseEntity.status(401).body(Map.of("message", "Token không hợp lệ", "error", e.getMessage()));
            }
        }

        @GetMapping("/check-email")
        public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestParam String email) {
            try {
                boolean exists = authService.checkEmailExists(email); // Kiểm tra email tồn tại trong DB
                Map<String, Object> response = new HashMap<>();
                response.put("exists", exists);
                return ResponseEntity.ok(response); // Trả về kiểu Map<String, Object>
            } catch (Exception e) {
                // Log lỗi chi tiết
                logger.error("Lỗi khi kiểm tra email: {}", email, e);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Đã có lỗi xảy ra khi kiểm tra email");
                return ResponseEntity.status(500).body(errorResponse);
            }
        }

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletResponse response    ) {
            try {

                // Đăng ký user
                authService.register(request);

                // Đăng nhập tự động
                LoginRequest loginRequest = new LoginRequest(request.getEmail(), request.getPassword());
                AuthResponse authResponse = authService.login(loginRequest);

                // Tạo cookie chứa token
                ResponseCookie cookie = ResponseCookie.from("token", authResponse.getToken())
                        .httpOnly(true)
                        .secure(false) // set true nếu dùng HTTPS
                        .path("/")
                        .maxAge(24 * 60 * 60)
                        .sameSite("Lax")
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                // Trả về message rõ ràng khi đăng ký thành công kèm theo data nếu cần
                return ResponseEntity.ok(Map.of(
                        "message", "Đăng ký thành công",
                        "data", authResponse
                ));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Đăng ký thất bại", "error", e.getMessage()));
            }
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {

            AuthResponse authResponse = authService.login(request);

            // Tạo cookie chứa token
            ResponseCookie cookie = ResponseCookie.from("token", authResponse.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(authResponse);
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(HttpServletResponse response) {

            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
        }

        @PostMapping("/change-password")
        public ResponseEntity<String> changePassword(
                @RequestParam String email,
                @RequestParam String oldPassword,
                @RequestParam String newPassword) {
            try {
                authService.changePassword(email, oldPassword, newPassword);
                return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
            }
        }

        @PostMapping("/verify-email")
        public ResponseEntity<?> verifyEmail(@RequestParam String token) {
            try {
                authService.verifyEmail(token);
                return ResponseEntity.ok(Map.of("message", "Xác thực email thành công"));
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("message", "Xác thực email thất bại", "error", e.getMessage()));
            }
        }
    }
