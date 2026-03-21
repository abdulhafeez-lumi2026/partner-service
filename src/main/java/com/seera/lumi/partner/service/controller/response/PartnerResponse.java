package com.seera.lumi.partner.service.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seera.lumi.partner.service.enums.BookingMode;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import com.seera.lumi.partner.service.enums.QuoteMode;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartnerResponse {

    private String partnerId;
    private String partnerCode;
    private String name;
    private String contactEmail;
    private String debtorCode;
    private String clientId;
    private PartnerStatus status;
    private Integer rateLimit;
    private BigDecimal commissionPercentage;
    private QuoteMode quoteMode;
    private BookingMode bookingMode;
    private String allowedBranches;
    private String allowedVehicleGroups;
    private String webhookUrl;
    private String ipWhitelist;
    private LocalDate contractValidUntil;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    /** Only populated on create response — the plain-text API key shown once */
    private String apiKey;

    /** Only populated on create response — the plain-text client secret shown once */
    private String clientSecret;
}
