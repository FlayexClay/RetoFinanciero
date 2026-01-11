package com.financial.productos.domain.mapper;

import com.financial.productos.domain.dto.ProductoFinancieroDTO;
import com.financial.productos.domain.entity.ProductoFinanciero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoFinancieroMapper {
    @Mapping(target = "tipoProducto", expression = "java(producto.getTipoProducto().name())")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "saldo", source = "saldo")
    ProductoFinancieroDTO toDTO(ProductoFinanciero producto);

    List<ProductoFinancieroDTO> toDTOList(List<ProductoFinanciero> productos);

}
