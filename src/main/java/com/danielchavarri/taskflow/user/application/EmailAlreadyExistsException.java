
package com.danielchavarri.taskflow.user.application;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("An account with this email already exists.");
    }
}