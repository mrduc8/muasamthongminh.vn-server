package mstm.muasamthongminh.muasamthongminh.modules.address.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.address.dto.AddressDto;
import mstm.muasamthongminh.muasamthongminh.modules.address.model.Address;
import mstm.muasamthongminh.muasamthongminh.modules.address.service.AddressService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<Address> addresses = addressService.getAddressesByUser(user);
        List<AddressDto> dtos = addresses.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@RequestBody AddressDto req, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        if (user == null) {
            return ResponseEntity.status(400).build();
        }

        Address address = Address.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .isDefault(Boolean.TRUE.equals(req.getIsDefault()))
                .user(user)
                .build();

        Address saved = addressService.createAddress(address, user);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> update(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @RequestBody AddressDto req) {
        User user = userDetails.getUser();
        Address updated = addressService.updateAddress(user, id, req);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        User user = userDetails.getUser();
        addressService.deleteAddress(id, user);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<?> setDefault(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        User user = userDetails.getUser();
        addressService.setDefaultAddress(user, id);
        return ResponseEntity.ok("Set default address successfully");
    }

    // Helper method để convert từ Address -> AddressDto
    private AddressDto toDto(Address address) {
        return AddressDto.builder()
                .name(address.getName())
                .phone(address.getPhone())
                .address(address.getAddress())
                .isDefault(address.getIsDefault())
                .build();
    }
}
