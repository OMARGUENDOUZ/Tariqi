package com.example.carly.exception;

public class ExamRegistrationException extends BusinessRuleException {

    public ExamRegistrationException(String message) {
        super("EXAM_REGISTRATION_ERROR", message);
    }
}
