package com.eden.msutils.mybatis.generator.exception;

public class MybatisGeneratorException extends RuntimeException {
    public MybatisGeneratorException() {
    }

    public MybatisGeneratorException(String message) {
        super(message);
    }

    public MybatisGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
