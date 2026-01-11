package com.financial.bff.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
public class EncryptionService {

    public Optional<String> decrypt(String encryptedCode, String traceId) {
        try {
            log.debug("[TraceId: {}] Desencriptando código", traceId);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedCode);
            String decrypted = new String(decodedBytes);

            log.debug("[TraceId: {}] Código desencriptado exitosamente", traceId);
            return Optional.of(decrypted);

        } catch (IllegalArgumentException e) {
            log.error("[TraceId: {}] Error al desencriptar código: {}",
                    traceId, e.getMessage());
            return Optional.empty();
        }
    }

    public String encrypt(String code) {
        return Base64.getEncoder().encodeToString(code.getBytes());
    }
}
