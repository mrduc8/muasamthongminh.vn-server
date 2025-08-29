package mstm.muasamthongminh.muasamthongminh.modules.brands.service;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.brands.dto.BrandDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BrandService {
    // Tạo thương hiệu mới
    ResponseEntity<?> createdBrands(BrandDto dto, User user);

    // Lấy đanh sách thương hiệu
    List<BrandDto> listBrands();

    // Sửa một thương hiệu cụ thể
    ResponseEntity<?> updatedBrands(BrandDto dto, User user);

}
