package com.seera.lumi.partner.service.repository;

import com.seera.lumi.partner.service.entity.Partner;
import com.seera.lumi.partner.service.enums.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByPartnerId(String partnerId);

    Optional<Partner> findByPartnerCode(String partnerCode);

    Optional<Partner> findByClientId(String clientId);

    Optional<Partner> findByApiKeyHash(String apiKeyHash);

    List<Partner> findAllByStatus(PartnerStatus status);

    Optional<Partner> findByDebtorCode(String debtorCode);
}
