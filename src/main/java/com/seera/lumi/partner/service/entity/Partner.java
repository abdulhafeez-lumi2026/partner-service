package com.seera.lumi.partner.service.entity;

import com.seera.lumi.partner.service.enums.BookingMode;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import com.seera.lumi.partner.service.enums.QuoteMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partner")
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_id", nullable = false, unique = true, length = 36)
    private String partnerId;

    @Column(name = "partner_code", nullable = false, unique = true, length = 50)
    private String partnerCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "debtor_code", nullable = false, length = 50)
    private String debtorCode;

    @Column(name = "api_key_hash")
    private String apiKeyHash;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret_hash")
    private String clientSecretHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PartnerStatus status = PartnerStatus.SANDBOX;

    @Column(name = "rate_limit", nullable = false)
    @Builder.Default
    private Integer rateLimit = 60;

    @Column(name = "commission_percentage", precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "quote_mode", nullable = false, length = 10)
    @Builder.Default
    private QuoteMode quoteMode = QuoteMode.BOTH;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_mode", nullable = false, length = 20)
    @Builder.Default
    private BookingMode bookingMode = BookingMode.PAY_LATER;

    @Column(name = "allowed_branches", columnDefinition = "TEXT")
    private String allowedBranches;

    @Column(name = "allowed_vehicle_groups", columnDefinition = "TEXT")
    private String allowedVehicleGroups;

    @Column(name = "promo_code", length = 100)
    private String promoCode;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Column(name = "ip_whitelist", columnDefinition = "TEXT")
    private String ipWhitelist;

    @Column(name = "contract_valid_until")
    private LocalDate contractValidUntil;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}
