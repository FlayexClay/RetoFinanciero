package com.financial.bff.cient;

import com.financial.bff.dto.ProductoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
@Slf4j
public class ProductosServiceClient {

    private final WebClient webClient;

    public ProductosServiceClient(
            @Value("${services.productos.url}") String productosServiceUrl,
            WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(productosServiceUrl)
                .build();
    }

    public Flux<ProductoDTO> obtenerProductos(String codigoCliente, String traceId) {
        log.info("[TraceId: {}] Llamando a Productos Service para cliente: {}",
                traceId, codigoCliente);

        return webClient
                .get()
                .uri("/api/v1/productos/cliente/{codigoCliente}", codigoCliente)
                .header("X-Trace-Id", traceId)
                .retrieve()
                .bodyToFlux(ProductoDTO.class)
                .timeout(Duration.ofSeconds(5))
                .doOnComplete(() ->
                        log.info("[TraceId: {}] Productos obtenidos exitosamente", traceId))
                .doOnError(error ->
                        log.error("[TraceId: {}] Error al obtener productos: {}",
                                traceId, error.getMessage()))
                .onErrorResume(throwable -> {
                    log.warn("[TraceId: {}] No se pudieron obtener productos, retornando lista vacía",
                            traceId);
                    return Flux.empty();
                });
    }
}