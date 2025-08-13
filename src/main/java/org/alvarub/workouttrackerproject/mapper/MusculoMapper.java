package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoEmbeddedDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Musculo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MusculoMapper {

    Musculo toEntity(MusculoRequestDTO dto);

    MusculoResponseDTO toResponseDTO(Musculo entity);

    MusculoSimpleDTO toSimpleDTO(Musculo entity);

    Musculo toEntityFromEmbedded(MusculoEmbeddedDTO dto);
}
