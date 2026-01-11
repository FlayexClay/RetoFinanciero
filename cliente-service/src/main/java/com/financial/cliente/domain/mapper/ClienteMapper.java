package com.financial.cliente.domain.mapper;

import com.financial.cliente.domain.dto.ClienteDTO;
import com.financial.cliente.domain.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "nombres", source = "nombres")
    @Mapping(target = "apellidos", source = "apellidos")
    @Mapping(target = "tipoDocumento", source = "tipoDocumento")
    @Mapping(target = "numeroDocumento", source = "numeroDocumento")
    ClienteDTO toDTO(Cliente cliente);
}
