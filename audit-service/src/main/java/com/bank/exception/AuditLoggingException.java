package com.bank.exception;

public class AuditLoggingException extends RuntimeException {

    private String errorCode;
    private Object details;

    public AuditLoggingException(String message) {
        super(message);
        this.errorCode = "AUDIT_LOGGING_ERROR";
    }

    public AuditLoggingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AUDIT_LOGGING_ERROR";
    }

    public AuditLoggingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuditLoggingException(String message, String errorCode, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getDetails() {
        return details;
    }
}
