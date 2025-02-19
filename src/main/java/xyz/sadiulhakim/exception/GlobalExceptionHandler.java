package xyz.sadiulhakim.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    String handleRuntimeError(RuntimeException e) {
        LOGGER.error("Error Occurred :: {}", e.getMessage());
        return "redirect:/error";
    }
}
