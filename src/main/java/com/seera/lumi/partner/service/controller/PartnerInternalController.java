package com.seera.lumi.partner.service.controller;

import com.seera.lumi.partner.service.controller.response.PartnerResponse;
import com.seera.lumi.partner.service.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/api/v1/partners")
@RequiredArgsConstructor
public class PartnerInternalController {

    private final PartnerService partnerService;

    @GetMapping("/{partnerCode}")
    public ResponseEntity<PartnerResponse> getPartner(@PathVariable String partnerCode) {
        return ResponseEntity.ok(partnerService.getPartner(partnerCode));
    }

    @PostMapping("/validate-api-key")
    public ResponseEntity<PartnerResponse> validateApiKey(@RequestBody Map<String, String> body) {
        String apiKey = body.get("apiKey");
        return ResponseEntity.ok(partnerService.validateApiKey(apiKey));
    }

    @GetMapping("/by-client-id/{clientId}")
    public ResponseEntity<PartnerResponse> getPartnerByClientId(@PathVariable String clientId) {
        return ResponseEntity.ok(partnerService.getPartnerByClientId(clientId));
    }
}
