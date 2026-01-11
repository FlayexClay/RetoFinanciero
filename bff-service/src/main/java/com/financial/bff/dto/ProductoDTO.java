package com.financial.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    @JsonProperty("tipoProducto")
    private String tipoProducto;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("saldo")
    private BigDecimal saldo;
}