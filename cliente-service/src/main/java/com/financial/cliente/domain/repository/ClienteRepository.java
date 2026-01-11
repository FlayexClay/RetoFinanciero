package com.financial.cliente.domain.repository;

import com.financial.cliente.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClienteRepository  extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCodigoUnicoAndActivoTrue(String codidoUnico);
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
}
