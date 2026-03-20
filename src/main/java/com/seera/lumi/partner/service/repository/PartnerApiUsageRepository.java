package com.seera.lumi.partner.service.repository;

import com.seera.lumi.partner.service.entity.PartnerApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerApiUsageRepository extends JpaRepository<PartnerApiUsage, Long> {
}
