package com.bank.service;

public class RuleViolation {
    private final boolean violated;
    private final String message;
    private final String severity;

    public RuleViolation(boolean violated, String message, String severity) {
        this.violated = violated;
        this.message = message;
        this.severity = severity;
    }

    public boolean violated() { return violated; }
    public String message() { return message; }
    public String severity() { return severity; }

    public static RuleViolation ok() { return new RuleViolation(false, null, null); }
    public static RuleViolation violated(String message, String severity) { return new RuleViolation(true, message, severity); }
}
