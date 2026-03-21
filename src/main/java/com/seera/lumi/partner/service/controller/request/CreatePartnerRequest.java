package com.seera.lumi.partner.service.controller.request;

import com.seera.lumi.partner.service.enums.BookingMode;
import com.seera.lumi.partner.service.enums.QuoteMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CreatePartnerRequest {

    @NotBlank(message = "Partner code is required")
    private String partnerCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    @NotBlank(message = "Debtor code is required")
    private String debtorCode;

    private BigDecimal commissionPercentage;

    private QuoteMode quoteMode;

    private BookingMode bookingMode;

    private String allowedBranches;

    private String allowedVehicleGroups;

    private String webhookUrl;

    private String ipWhitelist;

    private LocalDate contractValidUntil;
}
