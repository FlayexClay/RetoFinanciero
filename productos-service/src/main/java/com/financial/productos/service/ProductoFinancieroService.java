package com.financial.productos.service;

import com.financial.productos.domain.dto.ProductoFinancieroDTO;
import com.financial.productos.domain.entity.ProductoFinanciero;
import com.financial.productos.domain.mapper.ProductoFinancieroMapper;
import com.financial.productos.domain.repository.ProductoFinancieroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoFinancieroService {

    private final ProductoFinancieroRepository productoRepository;
    private final ProductoFinancieroMapper productoMapper;

    public Flux<ProductoFinancieroDTO> obtenerProductosPorCodigoCliente(
            String codigoCliente, String traceId) {

        log.info("[TraceId: {}] Buscando productos para cliente: {}",
                traceId, codigoCliente);

        return Flux.defer(() -> {
                    List<ProductoFinanciero> productos = productoRepository
                            .findByCodigoClienteAndActivoTrue(codigoCliente);

                    log.info("[TraceId: {}] Encontrados {} productos para cliente: {}",
                            traceId, productos.size(), codigoCliente);

                    List<ProductoFinancieroDTO> dtos = productos.stream()
                            .filter(ProductoFinanciero::getActivo)
                            .map(productoMapper::toDTO)
                            .collect(Collectors.toList());

                    return Flux.fromIterable(dtos);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("[TraceId: {}] Error al buscar productos: {}",
                        traceId, error.getMessage()));
    }
}
