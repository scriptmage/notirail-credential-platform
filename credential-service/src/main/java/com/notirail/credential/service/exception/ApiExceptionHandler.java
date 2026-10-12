package com.notirail.credential.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CredentialNotFoundException.class)
    public ProblemDetail handleCredentialNotFound(final CredentialNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Credential not found", exception.getMessage());
    }

    @ExceptionHandler(CredentialKeyNotFoundException.class)
    public ProblemDetail handleKeyNotFound(final CredentialKeyNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Credential key not found", exception.getMessage());
    }

    @ExceptionHandler(CredentialKeyAlreadyRevokedException.class)
    public ProblemDetail handleKeyAlreadyRevoked(final CredentialKeyAlreadyRevokedException exception) {
        return problem(
            HttpStatus.CONFLICT,
            "Credential key already revoked",
            exception.getMessage()
        );
    }

    private ProblemDetail problem(final HttpStatus status, final String title, final String detail) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }

}
