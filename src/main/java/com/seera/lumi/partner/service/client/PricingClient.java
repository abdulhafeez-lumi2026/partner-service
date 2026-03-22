package com.seera.lumi.partner.service.client;

import com.seera.lumi.partner.service.client.request.CreateQuoteRequest;
import com.seera.lumi.partner.service.client.request.SearchOffersRequest;
import com.seera.lumi.partner.service.client.response.RentalOffersResponse;
import com.seera.lumi.partner.service.client.response.VehicleQuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pricing-service", url = "${api.pricing.baseUrl}")
public interface PricingClient {

    @PutMapping("/api/pricing/offers")
    RentalOffersResponse searchOffers(@RequestBody SearchOffersRequest request);

    @PostMapping("/api/pricing/quote")
    VehicleQuoteResponse createQuote(@RequestBody CreateQuoteRequest request);
}
