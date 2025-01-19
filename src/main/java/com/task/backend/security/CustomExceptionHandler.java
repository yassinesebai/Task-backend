package com.task.backend.security;

import com.task.backend.dto.response.GlobalResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<String>> handleSecurityException(Exception ex) {
        ResponseEntity<GlobalResponse<String>> response = null;

        if (ex instanceof BadCredentialsException) {
            System.out.println("*************\n");
            System.out.println("invalid creds");
            response = new ResponseEntity<>(
                    new GlobalResponse<> (
                            "401", "Invalid Credentials"
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }

        if (ex instanceof AccessDeniedException) {
            System.out.println("*************\n");
            System.out.println("access denied");
            response = new ResponseEntity<> (
                    new GlobalResponse<> (
                            "403", "Access Denied"
                    ),
                    HttpStatus.FORBIDDEN
            );
        }

        if (ex instanceof SignatureException) {
            System.out.println("*************\n");
            System.out.println("access denied");
            response = new ResponseEntity<> (
                    new GlobalResponse<> (
                            "403", "JWT signature is not valid"
                    ),
                    HttpStatus.FORBIDDEN
            );
        }

        if (ex instanceof ExpiredJwtException) {
            System.out.println("*************\n");
            System.out.println("access denied");
            response = new ResponseEntity<> (
                    new GlobalResponse<> (
                            "403", "Token already expired"
                    ),
                    HttpStatus.FORBIDDEN
            );
        }

        if (ex instanceof LockedException) {
            System.out.println("*************\n");
            System.out.println("account locked");
            response = new ResponseEntity<> (
                    new GlobalResponse<> (
                            "423", "Your account has been blocked, please contact your account administrator !"
                    ),
                    HttpStatus.LOCKED
            );
        }

        return response;
    }

}