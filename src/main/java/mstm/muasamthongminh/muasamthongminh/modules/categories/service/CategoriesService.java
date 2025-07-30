package mstm.muasamthongminh.muasamthongminh.modules.categories.service;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.categories.dto.CategoriesDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoriesService {
    //Tạo danh mục mới từ người dùng
    ResponseEntity<?> createCategories(CategoriesDto categoriesDto, User user);

    //Lấy danh sách truy vấn theo điều kiện
    List<CategoriesDto> getCategoryTree();

    //Sửa đổi danh mục
    ResponseEntity<?> updateCategories(CategoriesDto categoriesDto, User user);

    //Xoá Danh mục
    ResponseEntity<?> deleteCategories(Long id, User user);


}
