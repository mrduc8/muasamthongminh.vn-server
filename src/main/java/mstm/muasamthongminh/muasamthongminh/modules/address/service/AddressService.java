package mstm.muasamthongminh.muasamthongminh.modules.address.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.address.dto.AddressDto;
import mstm.muasamthongminh.muasamthongminh.modules.address.model.Address;
import mstm.muasamthongminh.muasamthongminh.modules.address.repository.AddressRepository;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AuthUserRepository authUserRepository;

    public List<Address> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    public Address createAddress(Address address, User user) {
        address.setUser(user);
        String fullAddress = String.join(", ",
                address.getDetailedAddress(),
                address.getWard(),
                address.getDistrict(),
                address.getProvinceCity()
        );
        address.setAddress(fullAddress);
        return addressRepository.save(address);
    }

    public Address updateAddress(User user, Long id, AddressDto req) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setName(req.getName());
        address.setPhone(req.getPhone());
        address.setAddress(req.getAddress());
        address.setIsDefault(req.getIsDefault() != null ? req.getIsDefault() : false);
        return addressRepository.save(address);
    }

    public void deleteAddress(Long id, User user) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(User user, Long id) {
        List<Address> addresses = addressRepository.findByUser(user);
        for (Address addr : addresses) {
            addr.setIsDefault(false);
        }
        addressRepository.saveAll(addresses);

        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setIsDefault(true);
        addressRepository.save(address);
    }
}
