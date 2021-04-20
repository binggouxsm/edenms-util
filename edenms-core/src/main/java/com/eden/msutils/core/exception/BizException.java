package com.eden.msutils.core.exception;

public class BizException extends Exception {

    private int code;

    public BizException(String message) {
        super(message);
        this.code = ExceptionCode.BIZEXCEPTION.getValue();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
