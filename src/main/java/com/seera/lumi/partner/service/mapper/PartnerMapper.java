package com.seera.lumi.partner.service.mapper;

import com.seera.lumi.partner.service.controller.request.CreatePartnerRequest;
import com.seera.lumi.partner.service.controller.request.UpdatePartnerRequest;
import com.seera.lumi.partner.service.controller.response.PartnerResponse;
import com.seera.lumi.partner.service.entity.Partner;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PartnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partnerId", ignore = true)
    @Mapping(target = "apiKeyHash", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "clientSecretHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rateLimit", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Partner toEntity(CreatePartnerRequest request);

    @Mapping(target = "apiKey", ignore = true)
    PartnerResponse toResponse(Partner partner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "partnerId", ignore = true)
    @Mapping(target = "partnerCode", ignore = true)
    @Mapping(target = "apiKeyHash", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "clientSecretHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(UpdatePartnerRequest request, @MappingTarget Partner partner);
}
