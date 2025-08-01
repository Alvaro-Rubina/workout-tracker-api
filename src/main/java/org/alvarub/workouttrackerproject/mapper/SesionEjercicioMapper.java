package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio.SesionEjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio.SesionEjercicioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.SesionEjercicio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SesionMapper.class, EjercicioMapper.class})
public interface SesionEjercicioMapper {

    SesionEjercicio toEntity(SesionEjercicioRequestDTO dto);

    SesionEjercicioResponseDTO toResponseDTO(SesionEjercicio entity);
}
