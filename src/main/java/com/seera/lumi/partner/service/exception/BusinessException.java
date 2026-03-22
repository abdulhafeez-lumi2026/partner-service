package com.seera.lumi.partner.service.exception;

import java.text.MessageFormat;

public class BusinessException extends RuntimeException {

    private final BaseError baseError;

    public BusinessException(BaseError baseError) {
        super(baseError.getDesc());
        this.baseError = baseError;
    }

    public BusinessException(Throwable cause, BaseError baseError) {
        super(baseError.getDesc(), cause);
        this.baseError = baseError;
    }

    public BusinessException(BaseError baseError, Object... params) {
        super(MessageFormat.format(baseError.getDesc(), params));
        String description = MessageFormat.format(baseError.getDesc(), params);
        this.baseError = new BaseError(baseError.getCode(), description, baseError.getHttpStatus());
    }

    public BaseError getError() {
        return this.baseError;
    }
}
