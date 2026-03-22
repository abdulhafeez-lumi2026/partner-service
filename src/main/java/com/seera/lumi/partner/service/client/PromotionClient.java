package com.seera.lumi.partner.service.client;

import com.seera.lumi.partner.service.client.response.PromotionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pricing-promotions", url = "${api.pricing.baseUrl}")
public interface PromotionClient {

    @GetMapping("/api/promotions/code/{code}")
    PromotionResponse getPromotionByCode(@PathVariable("code") String code);
}
