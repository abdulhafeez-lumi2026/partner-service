package com.seera.lumi.partner.service.service;

import com.seera.lumi.partner.service.client.PricingClient;
import com.seera.lumi.partner.service.client.request.CreateQuoteRequest;
import com.seera.lumi.partner.service.client.request.SearchOffersRequest;
import com.seera.lumi.partner.service.client.response.RentalOffersResponse;
import com.seera.lumi.partner.service.client.response.VehicleQuoteResponse;
import com.seera.lumi.partner.service.controller.pricing.request.InternalAvailabilityRequest;
import com.seera.lumi.partner.service.controller.pricing.request.InternalQuoteRequest;
import com.seera.lumi.partner.service.controller.pricing.response.ActivePromotionResponse;
import com.seera.lumi.partner.service.controller.pricing.response.PricingPackage;
import com.seera.lumi.partner.service.controller.pricing.response.QuoteResponse;
import com.seera.lumi.partner.service.controller.pricing.response.VehicleAvailabilityResponse;
import com.seera.lumi.partner.service.exception.BaseError;
import com.seera.lumi.partner.service.exception.BusinessException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private static final String QUOTE_CACHE_PREFIX = "partner:quote:";
    private static final long QUOTE_TTL_MINUTES = 30;

    private final PricingClient pricingClient;
    private final PromotionCacheService promotionCacheService;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<VehicleAvailabilityResponse> searchAvailability(InternalAvailabilityRequest request) {
        long totalStart = System.currentTimeMillis();
        try {
            SearchOffersRequest offersRequest = SearchOffersRequest.builder()
                    .pickupBranchId(request.getPickupLocationId())
                    .dropOffBranchId(request.getDropoffLocationId())
                    .pickupDate(request.getPickupDateTime().toEpochSecond(ZoneOffset.UTC))
                    .dropOffDate(request.getDropoffDateTime().toEpochSecond(ZoneOffset.UTC))
                    .accountNo(request.getDebtorCode())
                    .build();

            long pricingStart = System.currentTimeMillis();
            RentalOffersResponse offersResponse = pricingClient.searchOffers(offersRequest);
            long pricingMs = System.currentTimeMillis() - pricingStart;
            log.info("[TIMING] pricing-service searchOffers: {}ms ({}s)", pricingMs, pricingMs / 1000.0);

            log.info("Pricing service response: {}", offersResponse);
            if (offersResponse == null || offersResponse.getData() == null) {
                return List.of();
            }

            List<String> allowedGroups = request.getAllowedVehicleGroups();

            long buildStart = System.currentTimeMillis();
            List<VehicleAvailabilityResponse> result = offersResponse.getData().stream()
                    .filter(RentalOffersResponse.VehicleOfferData::isAvailable)
                    .filter(offer -> allowedGroups == null || allowedGroups.isEmpty()
                            || allowedGroups.contains(String.valueOf(offer.getGroupId())))
                    .map(offer -> {
                        log.info("Offer groupId={}, dailyKmsAllowance={}, extraKmsCharge={}",
                                offer.getGroupId(), offer.getDailyKmsAllowance(), offer.getExtraKmsCharge());
                        List<PricingPackage> packages = buildPricingPackages(offer);

                        return VehicleAvailabilityResponse.builder()
                                .vehicleGroup(String.valueOf(offer.getGroupId()))
                                .dailyKmAllowance(offer.getDailyKmsAllowance())
                                .extraKmCharge(offer.getExtraKmsCharge())
                                .packages(packages)
                                .build();
                    })
                    .collect(Collectors.toList());
            long buildMs = System.currentTimeMillis() - buildStart;
            log.info("[TIMING] package building: {}ms ({}s)", buildMs, buildMs / 1000.0);

            long totalMs = System.currentTimeMillis() - totalStart;
            log.info("[TIMING] total searchAvailability: {}ms ({}s)", totalMs, totalMs / 1000.0);

            return result;
        } catch (FeignException e) {
            log.error("Failed to search availability: status={}, message={}", e.status(), e.getMessage(), e);
            throw new BusinessException(e, BaseError.AVAILABILITY_SEARCH_FAILED);
        }
    }

    public QuoteResponse createQuote(InternalQuoteRequest request) {
        long totalStart = System.currentTimeMillis();
        try {
            CreateQuoteRequest quoteRequest = CreateQuoteRequest.builder()
                    .pickupBranchId(request.getPickupLocationId())
                    .dropOffBranchId(request.getDropoffLocationId())
                    .pickupDateTime(request.getPickupDateTime().toEpochSecond(ZoneOffset.UTC))
                    .dropOffDateTime(request.getDropoffDateTime().toEpochSecond(ZoneOffset.UTC))
                    .vehicleGroupCode(request.getVehicleGroup())
                    .accountNo(request.getDebtorCode())
                    .build();

            long pricingStart = System.currentTimeMillis();
            VehicleQuoteResponse quoteResult = pricingClient.createQuote(quoteRequest);
            long pricingMs = System.currentTimeMillis() - pricingStart;
            log.info("[TIMING] pricing-service createQuote: {}ms ({}s)", pricingMs, pricingMs / 1000.0);

            String partnerQuoteId = UUID.randomUUID().toString();

            List<PricingPackage> packages = buildPricingPackagesFromQuote(quoteResult);
            String promoCode = extractPromoCode(quoteResult.getPriceDetails());

            QuoteResponse response = QuoteResponse.builder()
                    .quoteId(partnerQuoteId)
                    .vehicleGroup(request.getVehicleGroup())
                    .packages(packages)
                    .pickupLocation(String.valueOf(request.getPickupLocationId()))
                    .dropoffLocation(String.valueOf(request.getDropoffLocationId()))
                    .pickupDateTime(request.getPickupDateTime())
                    .dropoffDateTime(request.getDropoffDateTime())
                    .currency(quoteResult.getCurrency())
                    .validUntil(LocalDateTime.now().plusMinutes(QUOTE_TTL_MINUTES))
                    .promoCode(promoCode)
                    .build();

            // Cache quote
            long cacheStart = System.currentTimeMillis();
            String cacheKey = QUOTE_CACHE_PREFIX + partnerQuoteId;
            redisTemplate.opsForValue().set(cacheKey, response, QUOTE_TTL_MINUTES, TimeUnit.MINUTES);
            long cacheMs = System.currentTimeMillis() - cacheStart;
            log.info("[TIMING] redis cache write: {}ms ({}s)", cacheMs, cacheMs / 1000.0);

            long totalMs = System.currentTimeMillis() - totalStart;
            log.info("[TIMING] total createQuote: {}ms ({}s), quoteId={}, promoCode={}",
                    totalMs, totalMs / 1000.0, partnerQuoteId, promoCode);
            return response;
        } catch (FeignException e) {
            log.error("Failed to create quote: status={}, message={}", e.status(), e.getMessage(), e);
            throw new BusinessException(e, BaseError.QUOTE_CREATION_FAILED);
        }
    }

    public QuoteResponse getQuote(String quoteId) {
        String cacheKey = QUOTE_CACHE_PREFIX + quoteId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached == null) {
            throw new BusinessException(BaseError.QUOTE_NOT_FOUND, quoteId);
        }
        if (cached instanceof QuoteResponse response) {
            return response;
        }
        throw new BusinessException(BaseError.QUOTE_NOT_FOUND, quoteId);
    }

    @SuppressWarnings("unchecked")
    private List<PricingPackage> buildPricingPackages(RentalOffersResponse.VehicleOfferData offer) {
        List<PricingPackage> packages = new ArrayList<>();
        Map<String, Object> priceDetails = offer.getPriceDetails();

        BigDecimal finalPrice = parseBigDecimal(offer.getFinalPrice());
        BigDecimal vatPercentage = parseBigDecimal(offer.getVatPercentage());
        BigDecimal discountPercentage = parseBigDecimal(offer.getDiscountPercentage());
        BigDecimal cdwPerDay = parseBigDecimal(offer.getCdwPerDay());
        BigDecimal pricePerDay = parseBigDecimal(offer.getPricePerDay());
        int soldDays = parseIntSafe(offer.getSoldDays());

        // Check if pricing service applied a promotion (allDiscountDetails)
        Map<String, Object> allDiscount = priceDetails != null
                ? (Map<String, Object>) priceDetails.get("allDiscountDetails") : null;

        String promoCode = extractPromoCode(priceDetails);
        BigDecimal promoDiscountPct = null;

        if (allDiscount != null) {
            // Use pricing service's pre-calculated discounted values
            BigDecimal discFinalPrice = parseBigDecimal(String.valueOf(allDiscount.get("finalPrice")));
            BigDecimal discVat = parseBigDecimal(String.valueOf(allDiscount.get("vat")));
            BigDecimal discRentalSum = parseBigDecimal(String.valueOf(allDiscount.get("rentalSum")));
            BigDecimal discAmount = parseBigDecimal(String.valueOf(allDiscount.get("discount")));

            Map<String, Object> promoSummary = (Map<String, Object>) allDiscount.get("promotionSummary");
            if (promoSummary != null) {
                promoDiscountPct = parseBigDecimal(String.valueOf(promoSummary.get("discount")));
            }

            // PAY_AND_GO: rental + CDW (Collision Damage Waiver)
            // finalPrice from pricing already includes VAT (rentalSum + vat)
            packages.add(PricingPackage.builder()
                    .type("PAY_AND_GO")
                    .description("Rental with CDW insurance included")
                    .baseRate(discRentalSum.add(discAmount))
                    .discountPercentage(promoDiscountPct != null ? promoDiscountPct : BigDecimal.ZERO)
                    .discountAmount(discAmount)
                    .finalRate(discRentalSum)
                    .vatPercentage(vatPercentage)
                    .vatAmount(discVat)
                    .totalWithVat(discRentalSum.add(discVat))
                    .promoCode(promoCode)
                    .promoDiscountPercentage(promoDiscountPct)
                    .build());

            // RENTAL_ONLY: rental without CDW (subtract CDW from pre-VAT amounts)
            BigDecimal totalCdw = cdwPerDay.multiply(BigDecimal.valueOf(soldDays));
            BigDecimal basicBaseRate = (discRentalSum.add(discAmount)).subtract(totalCdw).max(BigDecimal.ZERO);
            BigDecimal basicDiscountAmount = basicBaseRate.multiply(promoDiscountPct != null ? promoDiscountPct : BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal basicFinalRate = discRentalSum.subtract(totalCdw).max(BigDecimal.ZERO);
            BigDecimal basicVatAmount = basicFinalRate.multiply(vatPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            packages.add(PricingPackage.builder()
                    .type("RENTAL_ONLY")
                    .description("Rental without CDW insurance")
                    .baseRate(basicBaseRate)
                    .discountPercentage(promoDiscountPct != null ? promoDiscountPct : BigDecimal.ZERO)
                    .discountAmount(basicDiscountAmount)
                    .finalRate(basicFinalRate)
                    .vatPercentage(vatPercentage)
                    .vatAmount(basicVatAmount)
                    .totalWithVat(basicFinalRate.add(basicVatAmount))
                    .promoCode(promoCode)
                    .promoDiscountPercentage(promoDiscountPct)
                    .build());
        } else {
            // No promotion — use original prices
            // finalPrice from pricing service already includes VAT
            BigDecimal fullBaseRate = pricePerDay.multiply(BigDecimal.valueOf(soldDays));
            BigDecimal fullDiscountAmount = fullBaseRate.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal fullRateBeforeVat = fullBaseRate.subtract(fullDiscountAmount);
            BigDecimal fullVatAmount = fullRateBeforeVat.multiply(vatPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            packages.add(PricingPackage.builder()
                    .type("PAY_AND_GO").description("Rental with CDW insurance included")
                    .baseRate(fullBaseRate).discountPercentage(discountPercentage)
                    .discountAmount(fullDiscountAmount).finalRate(fullRateBeforeVat).vatPercentage(vatPercentage)
                    .vatAmount(fullVatAmount).totalWithVat(fullRateBeforeVat.add(fullVatAmount)).build());

            BigDecimal totalCdw = cdwPerDay.multiply(BigDecimal.valueOf(soldDays));
            BigDecimal basicBaseRate = fullBaseRate.subtract(totalCdw).max(BigDecimal.ZERO);
            BigDecimal basicDiscountAmount = basicBaseRate.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal basicFinalRate = basicBaseRate.subtract(basicDiscountAmount);
            BigDecimal basicVatAmount = basicFinalRate.multiply(vatPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            packages.add(PricingPackage.builder()
                    .type("RENTAL_ONLY").description("Rental without CDW insurance")
                    .baseRate(basicBaseRate).discountPercentage(discountPercentage)
                    .discountAmount(basicDiscountAmount).finalRate(basicFinalRate).vatPercentage(vatPercentage)
                    .vatAmount(basicVatAmount).totalWithVat(basicFinalRate.add(basicVatAmount)).build());
        }

        return packages;
    }

    @SuppressWarnings("unchecked")
    private List<PricingPackage> buildPricingPackagesFromQuote(VehicleQuoteResponse quote) {
        // Reuse same logic — wrap quote as offer-like data
        RentalOffersResponse.VehicleOfferData offerData = RentalOffersResponse.VehicleOfferData.builder()
                .finalPrice(quote.getFinalPrice()).vatPercentage(quote.getVatPercentage())
                .discountPercentage(quote.getDiscountPercentage()).cdwPerDay(quote.getCdwPerDay())
                .pricePerDay(quote.getPricePerDay()).soldDays(quote.getSoldDays())
                .priceDetails(quote.getPriceDetails()).build();
        return buildPricingPackages(offerData);
    }

    @SuppressWarnings("unchecked")
    private String extractPromoCode(Map<String, Object> priceDetails) {
        if (priceDetails == null) return null;
        Map<String, Object> promoDetails = (Map<String, Object>) priceDetails.get("promotionDetails");
        if (promoDetails != null) {
            return (String) promoDetails.get("code");
        }
        return null;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private int parseIntSafe(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return new BigDecimal(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
