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
    private BigDecimal subtotal;
    private BigDecimal discountPercent;
    private BigDecimal discount;
    private BigDecimal totalBeforeVat;
    private BigDecimal vatPercent;
    private BigDecimal vat;
    private BigDecimal totalDue;
}
