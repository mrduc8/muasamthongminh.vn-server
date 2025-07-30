package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "shops_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "identity_number", length = 20, nullable = false)
    private String identityNumber;

    @Column(name = "issued_date", nullable = false)
    private String issuedDate;

    @Column(name = "exp_date", nullable = false)
    private String expDate;

    @Column(name = "issued_place", length = 100, nullable = false)
    private String issuedPlace;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "tax_code", length = 20)
    private String taxCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "license_file_url", columnDefinition = "TEXT")
    private String licenseFileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShopStatus status = ShopStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false, name = "create_at",updatable = false )
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updatedAt = createAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
