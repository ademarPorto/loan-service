package com.ademarporto.ls.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoanNotFoundException extends RuntimeException {
    private final String code;

    public LoanNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
