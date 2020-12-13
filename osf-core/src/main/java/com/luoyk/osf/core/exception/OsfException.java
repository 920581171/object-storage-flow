package com.luoyk.osf.core.exception;

/**
 * 定义osf类型异常
 */
public class OsfException extends RuntimeException{
    public OsfException() {
        super();
    }

    public OsfException(String message) {
        super(message);
    }

    public OsfException(String message, Throwable cause) {
        super(message, cause);
    }

    public OsfException(Throwable cause) {
        super(cause);
    }

    protected OsfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
