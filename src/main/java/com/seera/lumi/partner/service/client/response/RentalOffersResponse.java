package com.seera.lumi.partner.service.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalOffersResponse {

    private String searchId;
    private Long expiry;
    private Integer total;
    private List<VehicleOfferData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleOfferData {

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
}
