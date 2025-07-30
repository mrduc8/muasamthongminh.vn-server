package mstm.muasamthongminh.muasamthongminh.modules.categories.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.categories.dto.CategoriesDto;
import mstm.muasamthongminh.muasamthongminh.modules.categories.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoriesController {
    @Autowired
    private CategoriesService categoriesService;

    //List danh mục
    @GetMapping
    public ResponseEntity<?> getCategoryTree(){
        return ResponseEntity.ok(categoriesService.getCategoryTree());
    }

    //Tạo danh mục
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCategories(@ModelAttribute CategoriesDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        return categoriesService.createCategories(dto, user);
    }

    //Cập nhập danh mục
    @PutMapping(value = "update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategories(
            @PathVariable("id") Long id,
            @ModelAttribute CategoriesDto dto, Authentication authentication) {
        dto.setId(id);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        return categoriesService.updateCategories(dto, user);
    }

    //Xoá danh mục chi tiết
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategories(@PathVariable("id") Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        return categoriesService.deleteCategories(id, user);
    }

}
