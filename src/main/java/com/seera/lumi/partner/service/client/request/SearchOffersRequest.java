package com.seera.lumi.partner.service.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOffersRequest {

    private Long pickupBranchId;
    private Long dropOffBranchId;
    private Long pickupDate;
    private Long dropOffDate;
    private String accountNo;
}
