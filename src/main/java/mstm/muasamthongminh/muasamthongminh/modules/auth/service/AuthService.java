package mstm.muasamthongminh.muasamthongminh.modules.auth.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.common.enums.Status;
import mstm.muasamthongminh.muasamthongminh.common.service.MailService;
import mstm.muasamthongminh.muasamthongminh.config.JwtUtils;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.AuthResponse;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.LoginRequest;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.RegisterRequest;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.UserDto;
import mstm.muasamthongminh.muasamthongminh.modules.user.mapper.UserMapper;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MailService mailService;
    @Autowired
    private RoleRepository roleRepository;

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = authUserRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> {
                        logger.error("User not found with email: " + userDetails.getUsername());
                        return new RuntimeException("Invalid credentials");
                    });


            if (user.getStatus() != Status.ACTIVE && user.getStatus() != Status.PENDING) {
                return new AuthResponse(null, null, "Tài khoản không được phép đăng nhập");
            }

            List<String> roleNames = user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toList());

            UserDetails userDetailsWithRoles = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(roleNames.toArray(new String[0]))
                    .build();

            String token = jwtUtils.generateToken(userDetailsWithRoles, null, false);

            List<String> roles = user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toList());

            String message = (user.getStatus() == Status.PENDING)
                    ? "Email chưa xác thực. Bạn vẫn có thể đăng nhập nhưng cần xác thực để sử dụng đầy đủ chức năng."
                    : "Đăng nhập thành công";

            return new AuthResponse(token, roles, message);

        } catch (BadCredentialsException e) {
            logger.warn("Sai email hoặc mật khẩu: {}", request.getEmail(), e);
            throw new RuntimeException("Sai email hoặc mật khẩu");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Lỗi không xác định trong quá trình đăng nhập", e);
            throw new RuntimeException("Đăng nhập thất bại", e);
        }
    }

    public UserDto getUserInfoFromToken(String token) {
        // 1. Trích xuất email (hoặc username) từ token
        String email = jwtUtils.extractUsername(token);

        // 2. Tìm người dùng trong DB
        User user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với token đã cung cấp"));

        // 3. Trả về thông tin người dùng dạng DTO
        return UserMapper.toDto(user);
    }

    public void verifyEmail(String token) {
        String email = jwtUtils.extractUsername(token);
        User user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (user.getStatus() == Status.ACTIVE) {
            throw new IllegalStateException("Email đã được xác thực trước đó");
        }

        user.setStatus(Status.ACTIVE);
        authUserRepository.save(user);
    }

    public void requestNewEmail(String token, String newEmail) {
        String currentEmail = jwtUtils.extractUsername(token);

        User user = authUserRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (checkEmailExists(newEmail)) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        user.setEmail(newEmail);
        user.setStatus(Status.PENDING); // Cập nhật trạng thái
        authUserRepository.save(user);

        sendVerificationEmail(user); // Gửi tới email mới
    }

    public void register(RegisterRequest request) {
        if (authUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("email đã tồn tại");
        }
        if (!isPasswordStrong(request.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu quá yếu");
        }

        Roles buyerRole = roleRepository.findByName(Role.BUYER)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền"));


        System.out.println("ROLE: id=" + buyerRole.getId() + ", name=" + buyerRole.getName());

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(buyerRole));
        user.setStatus(Status.PENDING);
        user.setCreatedAt(LocalDateTime.now());

        authUserRepository.save(user);
        sendVerificationEmail(user);
    }

    public void sendVerificationEmail(User user) {
        String[] roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .toArray(String[]::new);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roleNames)
                .build();

        String token = jwtUtils.generateToken(userDetails, null, false);
        String verifyLink = "http://localhost:8080/api/auth/verify-email?token=" + token;

        String subject = "Xác thực tài khoản Mua Sắm Thông Minh";
        String content = "<h1>Xin chào " + user.getName() + "!</h1>" +
                "<p>Vui lòng xác thực email bằng cách nhấn vào: <a href=\"" + verifyLink + "\">liên kết này</a>.</p>";

        mailService.sendSimpleMail(user.getEmail(), subject, content);
        logger.info("Đã gửi email xác thực đến {}", user.getEmail());
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        // Kiểm tra xem mật khẩu mới có đủ mạnh hay không
        if (!isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới quá yếu. Vui lòng đảm bảo mật khẩu ít nhất 8 ký tự.");
        }

        // Tìm kiếm người dùng theo email
        User user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        authUserRepository.save(user);

        // Gửi email thông báo thay đổi mật khẩu
        String subject = "Thông báo thay đổi mật khẩu";
        String content = "<h1>Xin chào " + user.getName() + "!</h1>" +
                "<p>Mật khẩu của bạn đã được thay đổi thành công tại Mua Sắm Thông Minh.</p>";
        mailService.sendSimpleMail(user.getEmail(), subject, content);

        logger.info("Mật khẩu đã được thay đổi thành công cho người dùng: {}", user.getEmail());
    }

    @PostConstruct
    public void initAdminAccount() {
        if (authUserRepository.existsByEmailAndRoleName("admin@muasamthongminh.com", Role.ADMIN)) return;

        Roles adminRole = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

        User admin = new User();
        admin.setName("Mua Sắm Thông Minh");
        admin.setEmail("admin@muasamthongminh.com");
        admin.setPhone("0969741403");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(List.of(adminRole));
        admin.setStatus(Status.ACTIVE);
        admin.setCreatedAt(LocalDateTime.now());

        authUserRepository.save(admin);
    }

    public boolean checkEmailExists(String email) {
        return authUserRepository.existsByEmail(email);
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8;
    }
}
