package com.ZypLink.ZyplinkProj.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ZypLink.ZyplinkProj.exceptions.ApiErrorResponse;
import com.ZypLink.ZyplinkProj.exceptions.MethodArgumentNotValidException;
import com.ZypLink.ZyplinkProj.exceptions.ResourceNotFoundException;
import com.ZypLink.ZyplinkProj.exceptions.UrlValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================= BAD REQUEST ================= */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        400,
                        "BAD_REQUEST",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(UrlValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleUrlValidationException(
            UrlValidationException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        400,
                        "URL_VALIDATION_FAILED",
                        ex.getMessage()
                ));
    }

    /* ================= AUTH ================= */

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UsernameNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        401,
                        "UNAUTHORIZED",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleDisabledUser(
            DisabledException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(
                        403,
                        "ACCOUNT_DISABLED",
                        ex.getMessage()
                ));
    }

    /* ================= NOT FOUND ================= */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        404,
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage()
                ));
    }

    /* ================= RUNTIME ================= */

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(
            RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        400,
                        "RUNTIME_ERROR",
                        ex.getMessage()
                ));
    }

    /* ================= FALLBACK ================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "Something went wrong. Please try again later."
                ));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex) {

    
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponse(
                500,
                "MethodArgumentNotValidException",
                "Recheck the entered Arguments."
        ));
}



}
