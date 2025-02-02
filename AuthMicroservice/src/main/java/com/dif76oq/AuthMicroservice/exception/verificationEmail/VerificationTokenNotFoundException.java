package com.dif76oq.AuthMicroservice.exception.verificationEmail;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
