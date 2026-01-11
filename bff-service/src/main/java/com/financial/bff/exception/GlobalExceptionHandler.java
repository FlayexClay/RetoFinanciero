package com.financial.bff.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, ServerWebExchange exchange) {

        String traceId = exchange.getRequest().getHeaders()
                .getFirst("X-Trace-Id");

        log.error("[TraceId: {}] Argumento inválido: {}", traceId, ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                traceId
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            WebClientResponseException.NotFound ex, ServerWebExchange exchange) {

        String traceId = exchange.getRequest().getHeaders()
                .getFirst("X-Trace-Id");

        log.error("[TraceId: {}] Recurso no encontrado: {}", traceId, ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Cliente no encontrado",
                exchange.getRequest().getPath().value(),
                traceId
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceCommunicationException.class)
    public ResponseEntity<Map<String, Object>> handleServiceCommunication(
            ServiceCommunicationException ex, ServerWebExchange exchange) {

        String traceId = exchange.getRequest().getHeaders()
                .getFirst("X-Trace-Id");

        log.error("[TraceId: {}] Error de comunicación con servicio: {}",
                traceId, ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio temporalmente no disponible",
                exchange.getRequest().getPath().value(),
                traceId
        );

        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex, ServerWebExchange exchange) {

        String traceId = exchange.getRequest().getHeaders()
                .getFirst("X-Trace-Id");

        log.error("[TraceId: {}] Acceso denegado: {}", traceId, ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Acceso denegado",
                exchange.getRequest().getPath().value(),
                traceId
        );

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {

        String traceId = exchange.getRequest().getHeaders()
                .getFirst("X-Trace-Id");

        log.error("[TraceId: {}] Error interno del servidor: {}",
                traceId, ex.getMessage(), ex);

        Map<String, Object> body = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado",
                exchange.getRequest().getPath().value(),
                traceId
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> buildErrorResponse(
            HttpStatus status, String message, String path, String traceId) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        if (traceId != null) {
            body.put("traceId", traceId);
        }

        return body;
    }
}