package com.seera.lumi.partner.service.controller.pricing.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivePromotionResponse {

    private String code;
    private BigDecimal discountPercentage;
    private LocalDate validFrom;
    private LocalDate validTo;
}
