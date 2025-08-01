package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.ZonaMuscular;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MusculoMapper.class})
public interface ZonaMuscularMapper {

    ZonaMuscular toEntity(ZonaMuscularRequestDTO dto);

    ZonaMuscularResponseDTO toResponseDTO(ZonaMuscular entity);

    ZonaMuscularSimpleDTO toSimpleDTO(ZonaMuscular entity);
}
