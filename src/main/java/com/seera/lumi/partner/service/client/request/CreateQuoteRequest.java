package com.seera.lumi.partner.service.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuoteRequest {

    private Long pickupBranchId;
    private Long dropOffBranchId;
    private Long pickupDateTime;
    private Long dropOffDateTime;
    private Long vehicleGroupId;
    private String vehicleGroupCode;
    private String promoCode;
    private String accountNo;
    private Long insuranceId;
    private List<Long> addOnIds;
}
