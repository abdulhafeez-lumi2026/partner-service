package com.seera.lumi.partner.service.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponse {

    private String partnerCode;

    /** Plain-text API key — only shown once at rotation time */
    private String apiKey;
}
