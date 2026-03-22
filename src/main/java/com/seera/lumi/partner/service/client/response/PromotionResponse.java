package com.seera.lumi.partner.service.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {

    private Long id;
    private String code;
    private BigDecimal percentageDiscount;
    private boolean isEnabled;
    private boolean autoApplied;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate validFrom;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate validTo;

    private List<Long> branchIds;
    private List<String> carGroupCodes;
}
