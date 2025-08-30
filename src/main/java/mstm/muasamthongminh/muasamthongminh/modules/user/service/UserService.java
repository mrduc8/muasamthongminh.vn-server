package mstm.muasamthongminh.muasamthongminh.modules.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.UserDto;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.RoleRepository;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.repository.BankRepsitory;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.repository.ShopRequestsRepository;
import mstm.muasamthongminh.muasamthongminh.modules.user.dto.UserRequest;
import mstm.muasamthongminh.muasamthongminh.modules.user.mapper.UserMapper;
import mstm.muasamthongminh.muasamthongminh.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ShopRequestsRepository shopRequestsRepository;
    @Autowired private ShopRepository shopRepository;
    @Autowired private BankRepsitory bankRepsitory;

    @Autowired private Cloudinary cloudinary;

    // Lấy tất cả danh sách user
    public List<UserDto> getUser() {
        return userRepository.findAllWithRoles()
                .stream().map(UserMapper::toDto).toList();
    }

    // Cập nhập Role
    public ResponseEntity<?> updateRoleUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        Roles newRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Vai trò người dùng không tồn tại"));

        // Ghi đè toàn bộ vai trò (nếu muốn thêm thì dùng .add())
        user.setRoles(List.of(newRole));
        userRepository.save(user);

        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật vai trò thành công",
                "roles", roleNames
        ));
    }


    // Cập nhập thông tin user
    public ResponseEntity<?> updateDetailsUser(Long userId, UserDto userDto, MultipartFile avatarFile) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UserMapper.updateEntityFormUser(user, userDto);
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(avatarFile.getBytes(),
                        ObjectUtils.asMap("folder", "muasamthongminh/avatars"));

                String avatarUrl = (String) uploadResult.get("secure_url");
                user.setAvatar(avatarUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi upload avatar: " + e.getMessage());
            }
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Cập nhập thành công", "user", user.getId()));
    }

    // Cập nhật số điện thoại
    public ResponseEntity<?> updatePhone(String phone, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setPhone(phone);

        userRepository.save(user);

        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    //Cập nhập user"
    public UserRequest updateUser(String email, String name, String phone, MultipartFile avatarFile, String sex, String birthday) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setName(name);
        user.setPhone(phone);
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(avatarFile.getBytes(),
                        ObjectUtils.asMap("folder", "muasamthongminh/avatars"));

                String avatarUrl = (String) uploadResult.get("secure_url");
                user.setAvatar(avatarUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi upload avatar: " + e.getMessage());
            }
        }
        user.setSex(sex);
        user.setBirthday(birthday);

        userRepository.save(user);

        return new UserRequest(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getSex(),
                user.getBirthday()
        );
    }

    //Tìm kiếm
    public List<UserRequest> searchUsers(String searchQuery) {
        List<User> users = userRepository.findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(searchQuery, searchQuery);

        return users.stream().map(user -> new UserRequest(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getSex(),
                user.getBirthday()
        )).toList();
    }

    // Xoá user xoá tất cả dữ liệu
    public ResponseEntity<?> deleteUser(String id) {
        Long userId;

        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "ID người dùng không hợp lệ"));
        }

        // Kiểm tra user có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Xoá dữ liệu liên quan
        shopRequestsRepository.deleteByUserId(userId);
        shopRepository.deleteById(userId);
        bankRepsitory.deleteByUserId(userId);

        // Xoá user cuối cùng
        userRepository.delete(user);

        return ResponseEntity.ok(Map.of("message", "Xoá người dùng thành công"));
    }


    @Transactional
    public ResponseEntity<?> adminUpdateUser(Long userId, UserDto userDto, MultipartFile avatarFile) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (userDto.getEmail() != null && !userDto.getEmail().equalsIgnoreCase(user.getEmail())) {
            userRepository.findByEmail(userDto.getEmail()).ifPresent(exist -> {
                if (!exist.getId().equals(userId)) {
                    throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
                }
            });
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        if (userDto.getSex() != null) user.setSex(userDto.getSex());
        if (userDto.getBirthday() != null) user.setBirthday(userDto.getBirthday());
        if (userDto.getStatus() != null) user.setStatus(userDto.getStatus());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        avatarFile.getBytes(),
                        ObjectUtils.asMap("folder", "muasamthongminh/avatars")
                );
                String avatarUrl = (String) uploadResult.get("secure_url");
                user.setAvatar(avatarUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi upload avatar: " + e.getMessage(), e);
            }
        }

        // 4) Cập nhật ROLES nếu DTO có gửi (thay toàn bộ)
        if (userDto.getRoles() != null) {
            List<Role> requested = userDto.getRoles();

            // Lấy entity Roles tương ứng
            List<Roles> entities = roleRepository.findByNameIn(requested);

            // Kiểm tra thiếu role nào trong DB
            if (entities.size() != requested.size()) {
                List<Role> missing = requested.stream()
                        .filter(er -> entities.stream().noneMatch(e -> e.getName() == er))
                        .toList();
                throw new RuntimeException("Các vai trò không tồn tại trong DB: " + missing);
            }

            user.setRoles(entities); // ghi đè toàn bộ
        }

        // 5) Lưu & trả về DTO (đã map roles -> enum)
        user = userRepository.save(user);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }
}
