package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Sesion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SesionEjercicioMapper.class, RutinaMapper.class, CategoriaMapper.class})
public interface SesionMapper {

    @Mapping(target = "category.id", source = "categoryId")
    Sesion toEntity(SesionRequestDTO dto);

    SesionResponseDTO toResponseDTO(Sesion entity);

    SesionSimpleDTO toSimpleDTO(Sesion entity);
}
