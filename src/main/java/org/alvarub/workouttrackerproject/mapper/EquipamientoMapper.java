package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Equipamiento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipamientoMapper {

    Equipamiento toEntity(EquipamientoRequestDTO dto);

    EquipamientoResponseDTO toResponseDTO(Equipamiento entity);
}
