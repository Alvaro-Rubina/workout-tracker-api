package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoriaMapper.class, SesionMapper.class, UsuarioMapper.class})
public interface RutinaMapper {

    Rutina toEntity(RutinaRequestDTO dto);

    RutinaResponseDTO toResponseDTO(Rutina entity);

    RutinaSimpleDTO toSimpleDTO(Rutina entity);
}
