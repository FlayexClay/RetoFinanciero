package com.financial.cliente.service;

import com.financial.cliente.domain.dto.ClienteDTO;
import com.financial.cliente.domain.entity.Cliente;
import com.financial.cliente.domain.mapper.ClienteMapper;
import com.financial.cliente.domain.repository.ClienteRepository;
import com.financial.cliente.exception.ClienteNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    //Predicate para validar que el cliente esta activo
    private final Predicate<Cliente> isClienteActivo = Cliente::getActivo;

    public Mono<ClienteDTO> obtenerClientePorCodigoUnico(String codigoUnico, String traceId){
        log.info("[TraceId: {}] Buscando cliente con codigo unico: {}", traceId, codigoUnico);

        return Mono.fromCallable(()->{
            Optional<Cliente> clienteOpt = clienteRepository
                    .findByCodigoUnicoAndActivoTrue(codigoUnico);
            return clienteOpt
                    .filter(isClienteActivo)
                    .map(cliente -> {
                        log.info("[TraceId: {}] Cliente encontrado: {} {}", traceId , cliente.getNombres(), cliente.getApellidos());
                        return clienteMapper.toDTO(cliente);
                    })
                    .orElseThrow(() ->{
                        log.warn("[TraceId: {}] Cliente no encontrado con código: {}",
                                traceId, codigoUnico);
                        return new ClienteNotFoundException(
                                "Cliente no encontrado con código: " + codigoUnico);
                    });
        })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("[TraceId: {}] Errror al buscar cliente: {}",
                        traceId, error.getMessage()));

    }
}
