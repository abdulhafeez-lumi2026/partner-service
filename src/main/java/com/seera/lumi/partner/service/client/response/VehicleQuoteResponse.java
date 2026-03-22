package com.seera.lumi.partner.service.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleQuoteResponse {

    private String quoteId;
    private String offerId;
    private String finalPrice;
    private String currency;
    private String vatPercentage;
    private String discountPercentage;
    private String soldDays;
    private String cdwPerDay;
    private String pricePerDay;
    private Long groupId;
    private boolean available;
    private boolean cdwOn;
    private int dailyKmsAllowance;
    private double extraKmsCharge;
    private Map<String, Object> priceDetails;
    private Boolean creditCardRequired;
}
