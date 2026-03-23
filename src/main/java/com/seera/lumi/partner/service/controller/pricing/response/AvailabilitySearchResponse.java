package com.seera.lumi.partner.service.controller.pricing.response;

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
public class AvailabilitySearchResponse {
    private Long pickupLocationId;
    private Long dropoffLocationId;
    private LocalDateTime pickupDateTime;
    private LocalDateTime dropoffDateTime;
    private String promoCode;
    private int totalVehicles;
    private List<VehicleAvailabilityResponse> vehicles;
}
