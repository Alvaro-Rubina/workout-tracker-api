package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Peso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PesoMapper {

    Peso toEntity(PesoRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    PesoResponseDTO toResponseDTO(Peso entity);
}
