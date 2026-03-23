package com.seera.lumi.partner.service.controller.pricing.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuoteResponse {

    private String quoteId;
    private String vehicleGroup;
    private Long vehicleGroupId;
    private String pickupLocation;
    private String dropoffLocation;
    private String currency;
    private List<PricingPackage> packages;
    private LocalDateTime pickupDateTime;
    private LocalDateTime dropoffDateTime;
    private LocalDateTime validUntil;
    private String promoCode;
}
