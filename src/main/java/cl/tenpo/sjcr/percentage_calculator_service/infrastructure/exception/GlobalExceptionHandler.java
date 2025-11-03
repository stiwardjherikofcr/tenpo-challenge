package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.exception;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.InvalidInputException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.PercentageServiceUnavailableException;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(InvalidInputException.class)
        public ResponseEntity<ErrorResponseDto> handleInvalidInput(
                        InvalidInputException ex, HttpServletRequest request) {
                log.warn("Invalid input: {}", ex.getMessage());

                ErrorResponseDto error = ErrorResponseDto.builder()
                                .message("Invalid input")
                                .details(ex.getMessage())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDto> handleValidationErrors(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                log.warn("Validation failed: {}", ex.getMessage());

                String details = ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining(", "));

                ErrorResponseDto error = ErrorResponseDto.builder()
                                .message("Validation failed")
                                .details(details)
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
                        ConstraintViolationException ex, HttpServletRequest request) {
                log.warn("Constraint violation: {}", ex.getMessage());

                String details = ex.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));

                ErrorResponseDto error = ErrorResponseDto.builder()
                                .message("Invalid request parameters")
                                .details(details)
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(PercentageServiceUnavailableException.class)
        public ResponseEntity<ErrorResponseDto> handleServiceUnavailable(
                        PercentageServiceUnavailableException ex, HttpServletRequest request) {
                log.error("Service unavailable: {}", ex.getMessage());

                ErrorResponseDto error = ErrorResponseDto.builder()
                                .message("Service temporarily unavailable")
                                .details(ex.getMessage())
                                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDto> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unexpected error", ex);

                ErrorResponseDto error = ErrorResponseDto.builder()
                                .message("Internal server error")
                                .details("An unexpected error occurred")
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
