package com.seera.lumi.partner.service.controller;

import com.seera.lumi.partner.service.controller.pricing.request.InternalAvailabilityRequest;
import com.seera.lumi.partner.service.controller.pricing.request.InternalQuoteRequest;
import com.seera.lumi.partner.service.controller.pricing.response.AvailabilitySearchResponse;
import com.seera.lumi.partner.service.controller.pricing.response.QuoteResponse;
import com.seera.lumi.partner.service.service.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/internal/api/v1")
@RequiredArgsConstructor
public class PricingInternalController {

    private final PricingService pricingService;

    @PostMapping("/availability")
    public ResponseEntity<AvailabilitySearchResponse> searchAvailability(
            @RequestBody InternalAvailabilityRequest request) {
        return ResponseEntity.ok(pricingService.searchAvailability(request));
    }

    @PostMapping("/quote")
    public ResponseEntity<QuoteResponse> createQuote(@RequestBody InternalQuoteRequest request) {
        return ResponseEntity.ok(pricingService.createQuote(request));
    }

    @GetMapping("/quote/{quoteId}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String quoteId) {
        return ResponseEntity.ok(pricingService.getQuote(quoteId));
    }
}
