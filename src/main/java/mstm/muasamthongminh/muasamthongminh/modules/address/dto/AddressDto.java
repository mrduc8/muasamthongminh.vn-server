    package mstm.muasamthongminh.muasamthongminh.modules.address.dto;

    import lombok.*;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AddressDto {
        private String name;
        private String phone;
        private String provinceCity;
        private String district;
        private String ward;
        private String detailedAddress;
        private String address;
        private Boolean isDefault;
    }
