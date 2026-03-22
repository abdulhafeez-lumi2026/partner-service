package com.seera.lumi.partner.service.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseError {

    // Generic errors
    public static final BaseError INTERNAL_SERVER_ERROR =
            new BaseError(1001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final BaseError BAD_REQUEST =
            new BaseError(1002, "{0}", HttpStatus.BAD_REQUEST);
    public static final BaseError UNAUTHORIZED =
            new BaseError(1003, "Unauthorized", HttpStatus.UNAUTHORIZED);
    public static final BaseError ACCESS_DENIED =
            new BaseError(1004, "Access denied", HttpStatus.FORBIDDEN);
    public static final BaseError NOT_FOUND =
            new BaseError(1005, "{0} not found", HttpStatus.NOT_FOUND);
    public static final BaseError ALREADY_EXISTS =
            new BaseError(1006, "{0} already exists", HttpStatus.CONFLICT);
    public static final BaseError VALIDATION_ERROR =
            new BaseError(1007, "Validation failed: {0}", HttpStatus.BAD_REQUEST);

    // Partner-specific errors
    public static final BaseError PARTNER_NOT_FOUND =
            new BaseError(2001, "Partner not found: {0}", HttpStatus.NOT_FOUND);
    public static final BaseError PARTNER_SUSPENDED =
            new BaseError(2002, "Partner is suspended: {0}", HttpStatus.FORBIDDEN);
    public static final BaseError PARTNER_ALREADY_EXISTS =
            new BaseError(2003, "Partner already exists: {0}", HttpStatus.CONFLICT);

    // Pricing errors
    public static final BaseError PRICING_SERVICE_ERROR =
            new BaseError(3001, "Failed to fetch pricing: {0}", HttpStatus.BAD_GATEWAY);
    public static final BaseError AVAILABILITY_SEARCH_FAILED =
            new BaseError(3002, "Failed to search vehicle availability", HttpStatus.BAD_GATEWAY);
    public static final BaseError QUOTE_CREATION_FAILED =
            new BaseError(3003, "Failed to create quote", HttpStatus.BAD_GATEWAY);
    public static final BaseError QUOTE_NOT_FOUND =
            new BaseError(3004, "Quote not found or expired: {0}", HttpStatus.NOT_FOUND);

    // Promotion errors
    public static final BaseError PROMOTION_NOT_FOUND =
            new BaseError(4001, "Promotion not found: {0}", HttpStatus.NOT_FOUND);
    public static final BaseError PROMOTION_FETCH_FAILED =
            new BaseError(4002, "Failed to fetch promotion details", HttpStatus.BAD_GATEWAY);

    // Downstream service errors
    public static final BaseError DOWNSTREAM_SERVICE_ERROR =
            new BaseError(5001, "Downstream service error: {0}", HttpStatus.BAD_GATEWAY);

    protected String code;
    protected String desc;
    @JsonIgnore private HttpStatus httpStatus;

    protected BaseError(int code, String desc, HttpStatus httpStatus) {
        this.code = "PARTNER-" + code;
        this.desc = desc;
        this.httpStatus = httpStatus;
    }

    protected BaseError(String code, String desc, HttpStatus httpStatus) {
        this.code = code;
        this.desc = desc;
        this.httpStatus = httpStatus;
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.builder()
                .code(this.code)
                .message(this.desc)
                .build();
    }
}
