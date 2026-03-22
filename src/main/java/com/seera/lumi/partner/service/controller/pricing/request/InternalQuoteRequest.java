package com.seera.lumi.partner.service.controller.pricing.request;

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
public class InternalQuoteRequest {

    private Long pickupLocationId;
    private Long dropoffLocationId;
    private LocalDateTime pickupDateTime;
    private LocalDateTime dropoffDateTime;
    private String debtorCode;
    private List<String> allowedBranches;
    private List<String> allowedVehicleGroups;
    private String vehicleGroup;
}
