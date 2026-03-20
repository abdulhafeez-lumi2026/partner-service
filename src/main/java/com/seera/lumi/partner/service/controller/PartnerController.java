package com.seera.lumi.partner.service.controller;

import com.seera.lumi.partner.service.controller.request.CreatePartnerRequest;
import com.seera.lumi.partner.service.controller.request.UpdatePartnerRequest;
import com.seera.lumi.partner.service.controller.response.ApiKeyResponse;
import com.seera.lumi.partner.service.controller.response.PartnerResponse;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import com.seera.lumi.partner.service.service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping
    public ResponseEntity<PartnerResponse> createPartner(@Valid @RequestBody CreatePartnerRequest request) {
        PartnerResponse response = partnerService.createPartner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{partnerCode}")
    public ResponseEntity<PartnerResponse> getPartner(@PathVariable String partnerCode) {
        return ResponseEntity.ok(partnerService.getPartner(partnerCode));
    }

    @PutMapping("/{partnerCode}")
    public ResponseEntity<PartnerResponse> updatePartner(
            @PathVariable String partnerCode,
            @Valid @RequestBody UpdatePartnerRequest request) {
        return ResponseEntity.ok(partnerService.updatePartner(partnerCode, request));
    }

    @GetMapping
    public ResponseEntity<List<PartnerResponse>> listPartners(
            @RequestParam(required = false) PartnerStatus status) {
        return ResponseEntity.ok(partnerService.listPartners(status));
    }

    @PutMapping("/{partnerCode}/suspend")
    public ResponseEntity<Void> suspendPartner(@PathVariable String partnerCode) {
        partnerService.suspendPartner(partnerCode);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{partnerCode}/activate")
    public ResponseEntity<Void> activatePartner(@PathVariable String partnerCode) {
        partnerService.activatePartner(partnerCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{partnerCode}/rotate-api-key")
    public ResponseEntity<ApiKeyResponse> rotateApiKey(@PathVariable String partnerCode) {
        return ResponseEntity.ok(partnerService.rotateApiKey(partnerCode));
    }

    @PostMapping("/validate-api-key")
    public ResponseEntity<PartnerResponse> validateApiKey(@RequestBody Map<String, String> body) {
        String apiKey = body.get("apiKey");
        return ResponseEntity.ok(partnerService.validateApiKey(apiKey));
    }
}
