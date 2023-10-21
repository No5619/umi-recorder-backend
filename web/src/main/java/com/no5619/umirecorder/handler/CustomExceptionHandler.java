package com.no5619.umirecorder.handler;

import com.no5619.umirecorder.dto.MsgDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<MsgDto> UsernameNotFoundHandler(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MsgDto(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MsgDto> BadCredentialsHandler(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MsgDto(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<MsgDto> SecurityExceptionHandler(SecurityException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MsgDto(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }
}
