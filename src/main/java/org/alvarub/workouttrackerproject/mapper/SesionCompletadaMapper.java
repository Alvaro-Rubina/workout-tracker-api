package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.sesioncompletada.SesionCompletadaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.SesionCompletada;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, RutinaMapper.class})
public interface SesionCompletadaMapper {

    SesionCompletadaResponseDTO toResponseDTO(SesionCompletada dto);
}
