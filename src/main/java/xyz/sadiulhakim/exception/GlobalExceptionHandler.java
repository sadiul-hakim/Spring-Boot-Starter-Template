package xyz.sadiulhakim.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<?> handleRuntimeError(RuntimeException e) {
        LOGGER.error("Error Occurred :: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "message", "Unexpected error occurred!",
                                "error", e.getMessage()
                        )
                );
    }

    @ExceptionHandler(RequestNotPermitted.class)
    ResponseEntity<Map<String, String>> handleRequestNotPermitted(RequestNotPermitted e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "you have been blocked for making excessive calls!"));
    }
}
