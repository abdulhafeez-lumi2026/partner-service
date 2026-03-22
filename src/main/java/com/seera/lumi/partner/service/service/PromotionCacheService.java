package com.seera.lumi.partner.service.service;

import com.seera.lumi.partner.service.client.PromotionClient;
import com.seera.lumi.partner.service.client.response.PromotionResponse;
import com.seera.lumi.partner.service.controller.pricing.response.ActivePromotionResponse;
import com.seera.lumi.partner.service.entity.Partner;
import com.seera.lumi.partner.service.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionCacheService {

    private static final String PROMO_CACHE_PREFIX = "partner:promo:";

    private final PromotionClient promotionClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PartnerRepository partnerRepository;

    public ActivePromotionResponse getActivePromotion(String debtorCode) {
        // 1. Check Redis cache
        String cacheKey = PROMO_CACHE_PREFIX + debtorCode;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof ActivePromotionResponse response) {
            return response;
        }

        // 2. Look up partner's promo code
        Partner partner = partnerRepository.findByDebtorCode(debtorCode).orElse(null);
        if (partner == null || partner.getPromoCode() == null) {
            return null;
        }

        // 3. Fetch from pricing service
        try {
            PromotionResponse promo = promotionClient.getPromotionByCode(partner.getPromoCode());
            if (promo == null || !promo.isEnabled()) {
                return null;
            }
            LocalDate today = LocalDate.now();
            if (promo.getValidFrom() != null && today.isBefore(promo.getValidFrom())) {
                return null;
            }
            if (promo.getValidTo() != null && today.isAfter(promo.getValidTo())) {
                return null;
            }

            ActivePromotionResponse response = ActivePromotionResponse.builder()
                    .code(promo.getCode())
                    .discountPercentage(promo.getPercentageDiscount())
                    .validFrom(promo.getValidFrom())
                    .validTo(promo.getValidTo())
                    .build();

            // Cache with TTL until promo expiry
            long ttlDays = promo.getValidTo() != null
                    ? ChronoUnit.DAYS.between(today, promo.getValidTo()) + 1
                    : 1;
            redisTemplate.opsForValue().set(cacheKey, response, ttlDays, TimeUnit.DAYS);
            log.info("Cached promotion for debtorCode={}, code={}, ttlDays={}", debtorCode, promo.getCode(), ttlDays);
            return response;
        } catch (Exception e) {
            log.warn("Failed to fetch promotion for debtorCode={}: {}", debtorCode, e.getMessage());
            return null;
        }
    }
}
