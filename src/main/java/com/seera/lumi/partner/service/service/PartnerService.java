package com.seera.lumi.partner.service.service;

import com.seera.lumi.partner.service.controller.request.CreatePartnerRequest;
import com.seera.lumi.partner.service.controller.request.UpdatePartnerRequest;
import com.seera.lumi.partner.service.controller.response.ApiKeyResponse;
import com.seera.lumi.partner.service.controller.response.PartnerResponse;
import com.seera.lumi.partner.service.entity.Partner;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import com.seera.lumi.partner.service.exception.AuthenticationException;
import com.seera.lumi.partner.service.exception.PartnerAlreadyExistsException;
import com.seera.lumi.partner.service.exception.PartnerNotFoundException;
import com.seera.lumi.partner.service.exception.PartnerSuspendedException;
import com.seera.lumi.partner.service.mapper.PartnerMapper;
import com.seera.lumi.partner.service.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public PartnerResponse createPartner(CreatePartnerRequest request) {
        partnerRepository.findByPartnerCode(request.getPartnerCode())
                .ifPresent(existing -> {
                    throw new PartnerAlreadyExistsException(
                            "Partner with code '" + request.getPartnerCode() + "' already exists");
                });

        Partner partner = partnerMapper.toEntity(request);
        partner.setPartnerId(UUID.randomUUID().toString());

        // Generate and hash API key
        String plainApiKey = UUID.randomUUID().toString();
        partner.setApiKeyHash(passwordEncoder.encode(plainApiKey));

        // Generate client credentials
        partner.setClientId(UUID.randomUUID().toString());
        String plainClientSecret = "sk_" + UUID.randomUUID().toString().replace("-", "");
        partner.setClientSecretHash(passwordEncoder.encode(plainClientSecret));

        partner = partnerRepository.save(partner);
        log.info("Created partner: code={}, partnerId={}", partner.getPartnerCode(), partner.getPartnerId());

        PartnerResponse response = partnerMapper.toResponse(partner);
        response.setApiKey(plainApiKey);
        response.setClientSecret(plainClientSecret);
        return response;
    }

    @Transactional(readOnly = true)
    public PartnerResponse getPartner(String partnerCode) {
        Partner partner = findByPartnerCode(partnerCode);
        return partnerMapper.toResponse(partner);
    }

    @Transactional(readOnly = true)
    public PartnerResponse getPartnerByClientId(String clientId) {
        Partner partner = partnerRepository.findByClientId(clientId)
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner with clientId '" + clientId + "' not found"));
        return partnerMapper.toResponse(partner);
    }

    @Transactional
    public PartnerResponse updatePartner(String partnerCode, UpdatePartnerRequest request) {
        Partner partner = findByPartnerCode(partnerCode);
        partnerMapper.updateEntity(request, partner);
        partner = partnerRepository.save(partner);
        log.info("Updated partner: code={}", partnerCode);
        return partnerMapper.toResponse(partner);
    }

    @Transactional(readOnly = true)
    public List<PartnerResponse> listPartners(PartnerStatus status) {
        List<Partner> partners;
        if (status != null) {
            partners = partnerRepository.findAllByStatus(status);
        } else {
            partners = partnerRepository.findAll();
        }
        return partners.stream()
                .map(partnerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void suspendPartner(String partnerCode) {
        Partner partner = findByPartnerCode(partnerCode);
        partner.setStatus(PartnerStatus.SUSPENDED);
        partnerRepository.save(partner);
        log.info("Suspended partner: code={}", partnerCode);
    }

    @Transactional
    public void activatePartner(String partnerCode) {
        Partner partner = findByPartnerCode(partnerCode);
        partner.setStatus(PartnerStatus.ACTIVE);
        partnerRepository.save(partner);
        log.info("Activated partner: code={}", partnerCode);
    }

    @Transactional
    public ApiKeyResponse rotateApiKey(String partnerCode) {
        Partner partner = findByPartnerCode(partnerCode);

        String plainApiKey = UUID.randomUUID().toString();
        partner.setApiKeyHash(passwordEncoder.encode(plainApiKey));
        partnerRepository.save(partner);

        log.info("Rotated API key for partner: code={}", partnerCode);
        return ApiKeyResponse.builder()
                .partnerCode(partnerCode)
                .apiKey(plainApiKey)
                .build();
    }

    @Transactional(readOnly = true)
    public PartnerResponse validateApiKey(String apiKey) {
        // BCrypt does not allow lookup by hash directly, so we iterate over all partners
        // with an API key set. For production scale, consider a prefix-based lookup or cache.
        List<Partner> partners = partnerRepository.findAll();
        for (Partner partner : partners) {
            if (partner.getApiKeyHash() != null && passwordEncoder.matches(apiKey, partner.getApiKeyHash())) {
                return partnerMapper.toResponse(partner);
            }
        }
        throw new PartnerNotFoundException("Invalid API key");
    }

    @Transactional(readOnly = true)
    public Partner authenticatePartner(String clientId, String clientSecret) {
        Partner partner = partnerRepository.findByClientId(clientId)
                .orElseThrow(() -> new AuthenticationException("Invalid client credentials"));

        if (partner.getClientSecretHash() == null || !passwordEncoder.matches(clientSecret, partner.getClientSecretHash())) {
            throw new AuthenticationException("Invalid client credentials");
        }

        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new PartnerSuspendedException("Partner account is " + partner.getStatus().name().toLowerCase());
        }

        return partner;
    }

    @Transactional(readOnly = true)
    public Partner findByClientId(String clientId) {
        return partnerRepository.findByClientId(clientId)
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner with clientId '" + clientId + "' not found"));
    }

    private Partner findByPartnerCode(String partnerCode) {
        return partnerRepository.findByPartnerCode(partnerCode)
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner with code '" + partnerCode + "' not found"));
    }
}
