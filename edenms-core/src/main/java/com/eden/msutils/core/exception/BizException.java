package com.eden.msutils.core.exception;

public class BizException extends Throwable {

    private int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
