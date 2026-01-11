package com.financial.productos.domain.repository;

import com.financial.productos.domain.entity.ProductoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoFinancieroRepository extends JpaRepository<ProductoFinanciero, Long> {
    List<ProductoFinanciero> findByCodigoClienteAndActivoTrue(String codigoCliente);
}
