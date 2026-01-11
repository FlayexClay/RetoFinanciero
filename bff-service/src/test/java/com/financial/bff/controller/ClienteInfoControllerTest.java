package com.financial.bff.controller;

import com.financial.bff.dto.ClienteInfoResponse;
import com.financial.bff.service.EncryptionService;
import com.financial.bff.service.OrchestrationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


import java.util.List;
import java.util.Optional;


@WebFluxTest(controllers = ClienteInfoController.class)
class ClienteInfoControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private OrchestrationService orchestrationService;
    @MockitoBean
    private EncryptionService encryptionService;

    @Test
    void obtenerInformacionCliente_ok() {
        String codigoEncriptado = "Q0xJMDAx";
        String codigoDesencriptado = "CLI001";
        ClienteInfoResponse mockResponse = ClienteInfoResponse.builder().traceId("trace-123").nombres("Juan").apellidos("Pérez").tipoDocumento("DNI").numeroDocumento("12345678").productos(List.of()).build();

        Mockito.when(encryptionService.decrypt(Mockito.eq(codigoEncriptado), Mockito.anyString())).thenReturn(Optional.of(codigoDesencriptado));
        Mockito.when(orchestrationService.obtenerInformacionCompleta(Mockito.eq(codigoDesencriptado), Mockito.anyString())).thenReturn(Mono.just(mockResponse));
        webTestClient.mutateWith(mockJwt())
                .get().uri("/api/v1/clientes/info/{codigo}", codigoEncriptado).accept(MediaType.APPLICATION_JSON).header("X-Trace-Id", "trace-123").exchange().expectStatus().isOk().expectBody(ClienteInfoResponse.class).isEqualTo(mockResponse);
    }

    @Test
    void obtenerInformacionCliente_codigoInvalido() {
        String codigoEncriptado = "INVALID";

        Mockito.when(encryptionService.decrypt(Mockito.eq(codigoEncriptado), Mockito.anyString())).thenReturn(Optional.empty());

        webTestClient.mutateWith(mockJwt()).get().uri("/api/v1/clientes/info/{codigo}", codigoEncriptado).exchange().expectStatus().isBadRequest();
    }
}