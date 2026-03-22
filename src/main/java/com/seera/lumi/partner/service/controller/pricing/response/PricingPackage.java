package com.seera.lumi.partner.service.controller.pricing.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PricingPackage {

    private String type;
    private String description;
    private BigDecimal baseRate;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalRate;
    private BigDecimal vatPercentage;
    private BigDecimal vatAmount;
    private BigDecimal totalWithVat;
    private String promoCode;
    private BigDecimal promoDiscountPercentage;
}
