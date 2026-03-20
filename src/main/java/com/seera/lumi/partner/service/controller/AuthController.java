package com.seera.lumi.partner.service.controller;

import com.seera.lumi.partner.service.controller.request.TokenRequest;
import com.seera.lumi.partner.service.controller.response.TokenResponse;
import com.seera.lumi.partner.service.entity.Partner;
import com.seera.lumi.partner.service.service.JwtService;
import com.seera.lumi.partner.service.service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partner/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PartnerService partnerService;
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@Valid @RequestBody TokenRequest request) {
        Partner partner = partnerService.authenticatePartner(request.getClientId(), request.getClientSecret());

        String token = jwtService.generateToken(partner);

        TokenResponse response = TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs() / 1000)
                .partnerCode(partner.getPartnerCode())
                .build();

        return ResponseEntity.ok(response);
    }
}
