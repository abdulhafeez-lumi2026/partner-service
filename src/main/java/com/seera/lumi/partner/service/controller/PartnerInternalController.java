package com.seera.lumi.partner.service.controller;

import com.seera.lumi.partner.service.controller.response.PartnerResponse;
import com.seera.lumi.partner.service.service.JwtService;
import com.seera.lumi.partner.service.service.PartnerService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal/api/v1/partners")
@RequiredArgsConstructor
public class PartnerInternalController {

    private final PartnerService partnerService;
    private final JwtService jwtService;

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

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token is required"));
        }

        try {
            Claims claims = jwtService.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("partnerId", claims.getSubject());
            response.put("partnerCode", claims.get("partnerCode"));
            response.put("quoteMode", claims.get("quoteMode"));
            response.put("bookingMode", claims.get("bookingMode"));
            response.put("allowedBranches", claims.get("allowedBranches"));
            response.put("allowedVehicleGroups", claims.get("allowedVehicleGroups"));
            response.put("rateLimit", claims.get("rateLimit"));
            response.put("expiration", claims.getExpiration());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }
}
