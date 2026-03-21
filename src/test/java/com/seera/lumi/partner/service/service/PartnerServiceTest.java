package com.seera.lumi.partner.service.service;

import com.seera.lumi.partner.service.controller.request.CreatePartnerRequest;
import com.seera.lumi.partner.service.controller.request.UpdatePartnerRequest;
import com.seera.lumi.partner.service.controller.response.ApiKeyResponse;
import com.seera.lumi.partner.service.controller.response.PartnerResponse;
import com.seera.lumi.partner.service.entity.Partner;
import com.seera.lumi.partner.service.enums.BookingMode;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import com.seera.lumi.partner.service.enums.QuoteMode;
import com.seera.lumi.partner.service.exception.AuthenticationException;
import com.seera.lumi.partner.service.exception.PartnerAlreadyExistsException;
import com.seera.lumi.partner.service.exception.PartnerNotFoundException;
import com.seera.lumi.partner.service.exception.PartnerSuspendedException;
import com.seera.lumi.partner.service.mapper.PartnerMapper;
import com.seera.lumi.partner.service.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PartnerMapper partnerMapper;

    private PartnerService partnerService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Partner samplePartner;
    private PartnerResponse sampleResponse;
    private CreatePartnerRequest createRequest;

    @BeforeEach
    void setUp() {
        // Create service manually since passwordEncoder is a private final field with inline init
        partnerService = new PartnerService(partnerRepository, partnerMapper);

        samplePartner = Partner.builder()
                .id(1L)
                .partnerId("partner-uuid-123")
                .partnerCode("KAYAK")
                .name("Kayak")
                .contactEmail("partner@kayak.com")
                .debtorCode("DEB-KAYAK-001")
                .status(PartnerStatus.ACTIVE)
                .rateLimit(60)
                .commissionPercentage(new BigDecimal("5.00"))
                .quoteMode(QuoteMode.BOTH)
                .bookingMode(BookingMode.PAY_LATER)
                .allowedBranches("RUH,JED")
                .allowedVehicleGroups("ECAR,CCAR")
                .webhookUrl("https://kayak.com/webhook")
                .ipWhitelist("10.0.0.1")
                .contractValidUntil(LocalDate.of(2027, 12, 31))
                .build();

        sampleResponse = PartnerResponse.builder()
                .partnerId("partner-uuid-123")
                .partnerCode("KAYAK")
                .name("Kayak")
                .contactEmail("partner@kayak.com")
                .debtorCode("DEB-KAYAK-001")
                .status(PartnerStatus.ACTIVE)
                .rateLimit(60)
                .commissionPercentage(new BigDecimal("5.00"))
                .quoteMode(QuoteMode.BOTH)
                .bookingMode(BookingMode.PAY_LATER)
                .allowedBranches("RUH,JED")
                .allowedVehicleGroups("ECAR,CCAR")
                .webhookUrl("https://kayak.com/webhook")
                .ipWhitelist("10.0.0.1")
                .contractValidUntil(LocalDate.of(2027, 12, 31))
                .build();

        createRequest = CreatePartnerRequest.builder()
                .partnerCode("KAYAK")
                .name("Kayak")
                .contactEmail("partner@kayak.com")
                .debtorCode("DEB-KAYAK-001")
                .commissionPercentage(new BigDecimal("5.00"))
                .quoteMode(QuoteMode.BOTH)
                .bookingMode(BookingMode.PAY_LATER)
                .allowedBranches("RUH,JED")
                .allowedVehicleGroups("ECAR,CCAR")
                .webhookUrl("https://kayak.com/webhook")
                .ipWhitelist("10.0.0.1")
                .contractValidUntil(LocalDate.of(2027, 12, 31))
                .build();
    }

    @Test
    void createPartner_success_returnsApiKeyAndClientSecret() {
        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.empty());
        when(partnerMapper.toEntity(createRequest)).thenReturn(samplePartner);
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));
        when(partnerMapper.toResponse(any(Partner.class))).thenReturn(sampleResponse);

        PartnerResponse result = partnerService.createPartner(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getPartnerCode()).isEqualTo("KAYAK");
        assertThat(result.getApiKey()).isNotNull().isNotBlank();
        assertThat(result.getClientSecret()).isNotNull().startsWith("sk_");

        ArgumentCaptor<Partner> captor = ArgumentCaptor.forClass(Partner.class);
        verify(partnerRepository).save(captor.capture());
        Partner saved = captor.getValue();
        assertThat(saved.getPartnerId()).isNotNull();
        assertThat(saved.getApiKeyHash()).isNotNull();
        assertThat(saved.getClientId()).isNotNull();
        assertThat(saved.getClientSecretHash()).isNotNull();
    }

    @Test
    void createPartner_duplicateCode_throwsAlreadyExists() {
        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.of(samplePartner));

        assertThatThrownBy(() -> partnerService.createPartner(createRequest))
                .isInstanceOf(PartnerAlreadyExistsException.class)
                .hasMessageContaining("KAYAK");
    }

    @Test
    void getPartner_found() {
        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.of(samplePartner));
        when(partnerMapper.toResponse(samplePartner)).thenReturn(sampleResponse);

        PartnerResponse result = partnerService.getPartner("KAYAK");

        assertThat(result).isNotNull();
        assertThat(result.getPartnerCode()).isEqualTo("KAYAK");
        verify(partnerRepository).findByPartnerCode("KAYAK");
    }

    @Test
    void getPartner_notFound_throwsException() {
        when(partnerRepository.findByPartnerCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnerService.getPartner("UNKNOWN"))
                .isInstanceOf(PartnerNotFoundException.class)
                .hasMessageContaining("UNKNOWN");
    }

    @Test
    void updatePartner_success() {
        UpdatePartnerRequest updateRequest = UpdatePartnerRequest.builder()
                .name("Kayak Updated")
                .contactEmail("new@kayak.com")
                .build();

        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.of(samplePartner));
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));
        when(partnerMapper.toResponse(any(Partner.class))).thenReturn(sampleResponse);

        PartnerResponse result = partnerService.updatePartner("KAYAK", updateRequest);

        assertThat(result).isNotNull();
        verify(partnerMapper).updateEntity(eq(updateRequest), eq(samplePartner));
        verify(partnerRepository).save(samplePartner);
    }

    @Test
    void updatePartner_notFound_throwsException() {
        UpdatePartnerRequest updateRequest = UpdatePartnerRequest.builder().name("X").build();
        when(partnerRepository.findByPartnerCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnerService.updatePartner("UNKNOWN", updateRequest))
                .isInstanceOf(PartnerNotFoundException.class);
    }

    @Test
    void suspendPartner_success() {
        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.of(samplePartner));
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));

        partnerService.suspendPartner("KAYAK");

        assertThat(samplePartner.getStatus()).isEqualTo(PartnerStatus.SUSPENDED);
        verify(partnerRepository).save(samplePartner);
    }

    @Test
    void suspendPartner_notFound_throwsException() {
        when(partnerRepository.findByPartnerCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnerService.suspendPartner("UNKNOWN"))
                .isInstanceOf(PartnerNotFoundException.class);
    }

    @Test
    void activatePartner_success() {
        samplePartner.setStatus(PartnerStatus.SUSPENDED);
        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.of(samplePartner));
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));

        partnerService.activatePartner("KAYAK");

        assertThat(samplePartner.getStatus()).isEqualTo(PartnerStatus.ACTIVE);
        verify(partnerRepository).save(samplePartner);
    }

    @Test
    void rotateApiKey_success() {
        when(partnerRepository.findByPartnerCode("KAYAK")).thenReturn(Optional.of(samplePartner));
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));

        ApiKeyResponse result = partnerService.rotateApiKey("KAYAK");

        assertThat(result).isNotNull();
        assertThat(result.getPartnerCode()).isEqualTo("KAYAK");
        assertThat(result.getApiKey()).isNotNull().isNotBlank();
        // Verify the saved hash matches the returned plain key
        assertThat(passwordEncoder.matches(result.getApiKey(), samplePartner.getApiKeyHash())).isTrue();
        verify(partnerRepository).save(samplePartner);
    }

    @Test
    void rotateApiKey_notFound_throwsException() {
        when(partnerRepository.findByPartnerCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnerService.rotateApiKey("UNKNOWN"))
                .isInstanceOf(PartnerNotFoundException.class);
    }

    @Test
    void authenticatePartner_success() {
        String plainSecret = "sk_testsecret123";
        samplePartner.setClientId("client-id-123");
        samplePartner.setClientSecretHash(passwordEncoder.encode(plainSecret));
        samplePartner.setStatus(PartnerStatus.ACTIVE);

        when(partnerRepository.findByClientId("client-id-123")).thenReturn(Optional.of(samplePartner));

        Partner result = partnerService.authenticatePartner("client-id-123", plainSecret);

        assertThat(result).isNotNull();
        assertThat(result.getPartnerCode()).isEqualTo("KAYAK");
    }

    @Test
    void authenticatePartner_invalidClientId_throwsAuth() {
        when(partnerRepository.findByClientId("bad-client-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnerService.authenticatePartner("bad-client-id", "secret"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid client credentials");
    }

    @Test
    void authenticatePartner_invalidSecret_throwsAuth() {
        samplePartner.setClientId("client-id-123");
        samplePartner.setClientSecretHash(passwordEncoder.encode("correct-secret"));
        samplePartner.setStatus(PartnerStatus.ACTIVE);

        when(partnerRepository.findByClientId("client-id-123")).thenReturn(Optional.of(samplePartner));

        assertThatThrownBy(() -> partnerService.authenticatePartner("client-id-123", "wrong-secret"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid client credentials");
    }

    @Test
    void authenticatePartner_suspended_throwsSuspended() {
        String plainSecret = "sk_testsecret123";
        samplePartner.setClientId("client-id-123");
        samplePartner.setClientSecretHash(passwordEncoder.encode(plainSecret));
        samplePartner.setStatus(PartnerStatus.SUSPENDED);

        when(partnerRepository.findByClientId("client-id-123")).thenReturn(Optional.of(samplePartner));

        assertThatThrownBy(() -> partnerService.authenticatePartner("client-id-123", plainSecret))
                .isInstanceOf(PartnerSuspendedException.class)
                .hasMessageContaining("suspended");
    }

    @Test
    void validateApiKey_success() {
        String plainKey = "test-api-key-uuid";
        samplePartner.setApiKeyHash(passwordEncoder.encode(plainKey));

        when(partnerRepository.findAll()).thenReturn(List.of(samplePartner));
        when(partnerMapper.toResponse(samplePartner)).thenReturn(sampleResponse);

        PartnerResponse result = partnerService.validateApiKey(plainKey);

        assertThat(result).isNotNull();
        assertThat(result.getPartnerCode()).isEqualTo("KAYAK");
    }

    @Test
    void validateApiKey_invalid_throwsNotFound() {
        samplePartner.setApiKeyHash(passwordEncoder.encode("real-key"));

        when(partnerRepository.findAll()).thenReturn(List.of(samplePartner));

        assertThatThrownBy(() -> partnerService.validateApiKey("wrong-key"))
                .isInstanceOf(PartnerNotFoundException.class)
                .hasMessageContaining("Invalid API key");
    }
}
