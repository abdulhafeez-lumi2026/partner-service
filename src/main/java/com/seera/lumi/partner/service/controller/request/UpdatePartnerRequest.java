package com.seera.lumi.partner.service.controller.request;

import com.seera.lumi.partner.service.enums.BookingMode;
import com.seera.lumi.partner.service.enums.QuoteMode;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePartnerRequest {

    private String name;

    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    private String debtorCode;

    private BigDecimal commissionPercentage;

    private QuoteMode quoteMode;

    private BookingMode bookingMode;

    private String allowedBranches;

    private String allowedVehicleGroups;

    private String webhookUrl;

    private String ipWhitelist;

    private LocalDate contractValidUntil;

    private Integer rateLimit;
}
