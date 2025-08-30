package mstm.muasamthongminh.muasamthongminh.modules.user.mapper;

import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.modules.auth.dto.UserDto;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;

import java.util.List;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setAvatar(dto.getAvatar());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        user.setSex(dto.getSex());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddresses(dto.getAddresses());
        user.setStatus(dto.getStatus());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        return user;
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setAvatar(user.getAvatar());
        userDto.setName(user.getName());
        userDto.setSex(user.getSex());
        userDto.setBirthday(user.getBirthday());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setAddresses(user.getAddresses());
        userDto.setStatus(user.getStatus());
        List<Role> roleEnums = (user.getRoles() == null)
                ? List.of()
                : user.getRoles().stream()
                .map(Roles::getName)
                .toList();
        userDto.setRoles(roleEnums);
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }

    public static void updateEntityFormUser(User user, UserDto userDto) {
        if (user == null || userDto == null) return;

        user.setAvatar(userDto.getAvatar());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());
        user.setSex(userDto.getSex());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setAddresses(userDto.getAddresses());
        user.setStatus(userDto.getStatus());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setUpdatedAt(userDto.getUpdatedAt());

    }

}
