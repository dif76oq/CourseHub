package com.dif76oq.AuthMicroservice.exception.verificationEmail;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}
