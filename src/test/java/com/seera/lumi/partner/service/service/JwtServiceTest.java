package com.seera.lumi.partner.service.service;

import com.seera.lumi.partner.service.entity.Partner;
import com.seera.lumi.partner.service.enums.BookingMode;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import com.seera.lumi.partner.service.enums.QuoteMode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "lumi-partner-jwt-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256";
    private static final long EXPIRATION_MS = 3600000L;

    private JwtService jwtService;
    private Partner samplePartner;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);

        samplePartner = Partner.builder()
                .id(1L)
                .partnerId("partner-uuid-123")
                .partnerCode("KAYAK")
                .name("Kayak")
                .contactEmail("partner@kayak.com")
                .debtorCode("DEB-KAYAK-001")
                .status(PartnerStatus.ACTIVE)
                .rateLimit(120)
                .quoteMode(QuoteMode.FULL)
                .bookingMode(BookingMode.PAY_NOW)
                .allowedBranches("RUH,JED")
                .allowedVehicleGroups("ECAR,CCAR")
                .build();
    }

    @Test
    void generateToken_containsExpectedClaims() {
        String token = jwtService.generateToken(samplePartner);

        assertThat(token).isNotNull().isNotBlank();

        Claims claims = jwtService.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo("partner-uuid-123");
        assertThat(claims.get("partnerCode", String.class)).isEqualTo("KAYAK");
        assertThat(claims.get("debtorCode", String.class)).isEqualTo("DEB-KAYAK-001");
        assertThat(claims.get("quoteMode", String.class)).isEqualTo("FULL");
        assertThat(claims.get("bookingMode", String.class)).isEqualTo("PAY_NOW");
        assertThat(claims.get("allowedBranches", String.class)).isEqualTo("RUH,JED");
        assertThat(claims.get("allowedVehicleGroups", String.class)).isEqualTo("ECAR,CCAR");
        assertThat(claims.get("rateLimit", Integer.class)).isEqualTo(120);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void validateToken_validToken_returnsClaims() {
        String token = jwtService.generateToken(samplePartner);

        Claims claims = jwtService.validateToken(token);

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("partner-uuid-123");
        assertThat(claims.get("partnerCode", String.class)).isEqualTo("KAYAK");
    }

    @Test
    void validateToken_expiredToken_throwsException() {
        JwtService expiredJwtService = new JwtService(SECRET, -1L);
        String token = expiredJwtService.generateToken(samplePartner);

        assertThatThrownBy(() -> jwtService.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void validateToken_invalidSignature_throwsException() {
        String differentSecret = "a-completely-different-secret-key-that-is-at-least-256-bits-long-for-hmac";
        JwtService otherJwtService = new JwtService(differentSecret, EXPIRATION_MS);
        String token = otherJwtService.generateToken(samplePartner);

        assertThatThrownBy(() -> jwtService.validateToken(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void getExpirationMs_returnsConfigured() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }
}
