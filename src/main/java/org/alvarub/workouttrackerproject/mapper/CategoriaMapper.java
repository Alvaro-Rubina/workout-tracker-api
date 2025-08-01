package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequestDTO dto);

    CategoriaResponseDTO toResponseDTO(Categoria entity);
}
