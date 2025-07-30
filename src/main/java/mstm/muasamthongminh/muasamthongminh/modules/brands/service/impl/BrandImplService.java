package mstm.muasamthongminh.muasamthongminh.modules.brands.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.service.ImageUploadService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.brands.dto.BrandDto;
import mstm.muasamthongminh.muasamthongminh.modules.brands.mapper.BrandMapper;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import mstm.muasamthongminh.muasamthongminh.modules.brands.repository.BrandRepository;
import mstm.muasamthongminh.muasamthongminh.modules.brands.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandImplService implements BrandService {

    @Autowired private BrandRepository brandRepository;
    @Autowired private ImageUploadService imageUploadService;
    @Autowired private AuthUserRepository authUserRepository;

    // Tạo thương hiệu mới
    @Override
    public ResponseEntity<?> createdBrands(BrandDto dto, User user){
        user = authUserRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (dto.getImage() == null && dto.getImage().isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(dto.getImage());
                dto.setImageUrl(imageUrl);
            } catch (IOException e) {
                return new ResponseEntity<>(
                        Map.of("message", "Tải ảnh không thành công: " + e.getMessage()),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        Brands req = BrandMapper.toEntity(dto, user);
        brandRepository.save(req);
        return ResponseEntity.ok(BrandMapper.toDto(req));
    }

    // Lấy danh sách của brands
    @Override
    public List<BrandDto> listBrands() {
        return brandRepository.findAll().stream().map(BrandMapper::toDto).collect(Collectors.toList());
    }

    // Sửa một danh mục cụ thể
    @Override
    public ResponseEntity<?> updatedBrands(BrandDto dto, User user){
        user = authUserRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
        Brands extting = brandRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại!"));

        if (dto.getImage() == null && dto.getImage().isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(dto.getImage());
                dto.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Tải ảnh không thành cống: " + e.getMessage());
            }
        }
        if (extting.getName() != null) extting.setName(dto.getName());
        if (extting.getSlug() != null) extting.setSlug(dto.getSlug());
        if (extting.getDescription() != null) extting.setDescription(dto.getDescription());
        if (extting.getStatus() != null) extting.setStatus(dto.getStatus());
        if (dto.getUpdatedAt() != null) extting.setUpdatedAt(dto.getUpdatedAt());
        extting.setUpdatedByUser(user);
        extting.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());

        brandRepository.save(extting);
        return ResponseEntity.ok(BrandMapper.toDto(extting));
    }
}
