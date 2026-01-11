package com.financial.bff.cient;

import com.financial.bff.dto.ClienteDTO;
import com.financial.bff.exception.ServiceCommunicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class ClienteServiceClient {

    private final WebClient webClient;

    public ClienteServiceClient(
            @Value("${services.cliente.url}") String clienteServiceUrl,
            WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(clienteServiceUrl)
                .build();
    }

    public Mono<ClienteDTO> obtenerCliente(String codigoUnico, String traceId) {
        log.info("[TraceId: {}] Llamando a Cliente Service para código: {}",
                traceId, codigoUnico);

        return webClient
                .get()
                .uri("/api/v1/clientes/{codigoUnico}", codigoUnico)
                .header("X-Trace-Id", traceId)
                .retrieve()
                .bodyToMono(ClienteDTO.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(cliente ->
                        log.info("[TraceId: {}] Cliente obtenido exitosamente: {} {}",
                                traceId, cliente.getNombres(), cliente.getApellidos()))
                .doOnError(error ->
                        log.error("[TraceId: {}] Error al obtener cliente: {}",
                                traceId, error.getMessage()))
                .onErrorMap(throwable ->
                        new ServiceCommunicationException(
                                "Error al comunicarse con Cliente Service", throwable));
    }
}