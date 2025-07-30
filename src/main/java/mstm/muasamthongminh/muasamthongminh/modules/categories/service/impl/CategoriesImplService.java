package mstm.muasamthongminh.muasamthongminh.modules.categories.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.service.ImageUploadService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.categories.dto.CategoriesDto;
import mstm.muasamthongminh.muasamthongminh.modules.categories.mapper.CategoriesMapper;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import mstm.muasamthongminh.muasamthongminh.modules.categories.repository.CategoriesRepository;
import mstm.muasamthongminh.muasamthongminh.modules.categories.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriesImplService implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private AuthUserRepository authUserRepository;

    //Tạo danh mục mới
    @Override
    public ResponseEntity<?> createCategories(CategoriesDto dto, User user) {
        user = authUserRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
        //Upload ảnh
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(dto.getImage());
                dto.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
            }
        }

        Categories req = CategoriesMapper.toEntity(dto, user);
        categoriesRepository.save(req);
        return ResponseEntity.ok(CategoriesMapper.toDto(req));
    }

    //Danh sách danh mục
    @Override
    public List<CategoriesDto> getCategoryTree() {
        List<Categories> all = categoriesRepository.findAll();

        // Nhóm theo parentId
        Map<Long, List<Categories>> grouped = all.stream().collect(Collectors.groupingBy(cat -> cat.getParentId() == null ? 0L : cat.getParentId()));
        return buildTree(0L, grouped);
    }

    private List<CategoriesDto> buildTree(Long parentId, Map<Long, List<Categories>> grouped) {
        List<Categories> children = grouped.getOrDefault(parentId, Collections.emptyList());

        return children.stream().map(cat -> {
            CategoriesDto dto = CategoriesMapper.toDto(cat);
            dto.setChildren(buildTree(cat.getId(), grouped));
            return dto;
        }).collect(Collectors.toList());
    }

    //Cập nhập danh mục
    @Override
    public ResponseEntity<?> updateCategories(CategoriesDto dto, User user) {
        user = authUserRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Categories exting = categoriesRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại!"));

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(dto.getImage());
                exting.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi update ảnh:" + e.getMessage());
            }
        }

        if (exting.getName() != null) exting.setName(dto.getName());
        if (exting.getSlug() != null) exting.setSlug(dto.getSlug());
        if (dto.getParentId() != null) exting.setParentId(dto.getParentId());
        if (exting.getSortOrder() != null) exting.setSortOrder(exting.getSortOrder());
        if (exting.getStatus() != null) exting.setStatus(exting.getStatus());
        if (exting.getDescription() != null) exting.setDescription(exting.getDescription());
        if (exting.getMetaTitle() != null) exting.setMetaTitle(exting.getMetaTitle());
        if (exting.getMetaDescription() != null) exting.setMetaDescription(exting.getMetaDescription());
        exting.setUpdatedByUserId(user);
        exting.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());

        categoriesRepository.save(exting);
        return ResponseEntity.ok(CategoriesMapper.toDto(exting));
    }

    //Xoá danh mục
    @Override
    public ResponseEntity<?> deleteCategories(Long id, User user) {
        user = authUserRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tìm danh mục cần xoá
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        List<Categories> children = categoriesRepository.findByParentId(category.getId());
        if (!children.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Không thể xoá vì danh mục đang có danh mục con");
        }

        categoriesRepository.delete(category);

        return ResponseEntity.ok("Xoá danh mục thành công");
    }


}
