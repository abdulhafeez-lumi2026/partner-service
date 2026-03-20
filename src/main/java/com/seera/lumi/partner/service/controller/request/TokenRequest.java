package com.seera.lumi.partner.service.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    @NotBlank(message = "clientId is required")
    private String clientId;

    @NotBlank(message = "clientSecret is required")
    private String clientSecret;
}
