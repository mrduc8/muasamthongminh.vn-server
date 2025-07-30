package mstm.muasamthongminh.muasamthongminh.modules.brands.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.brands.dto.BrandDto;
import mstm.muasamthongminh.muasamthongminh.modules.brands.mapper.BrandMapper;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import mstm.muasamthongminh.muasamthongminh.modules.brands.repository.BrandRepository;
import mstm.muasamthongminh.muasamthongminh.modules.brands.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandService brandService;

    // Lấy danh sách chi tiết của một thương hiệu
    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Brands> brand = brandRepository.findById(id);

        if(brand.isPresent()){
            Brands brands = brand.get();
            return ResponseEntity.ok(BrandMapper.toDto(brands));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Thương hiệu không tồn tại!"));
        }
    }

    // Tạo thương hiệu
    @PostMapping(value = "/created", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBrand(@ModelAttribute BrandDto dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return brandService.createdBrands(dto, user);
    }

    // Lấy danh sách thương hiệu
    @GetMapping
    public List<BrandDto> listBrands(){
        return brandService.listBrands();
    }

    // Sửa một danh mục cụ thể
    @PostMapping(value = "/updated/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody BrandDto dto, Authentication authentication){
        dto.setId(id);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        return brandService.updatedBrands(dto, user);
    }
}
