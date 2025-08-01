package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Ejercicio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MusculoMapper.class, EquipamientoMapper.class})
public interface EjercicioMapper {

    Ejercicio toEntity(EjercicioRequestDTO dto);

    EjercicioResponseDTO toResponseDTO(Ejercicio entity);

    EjercicioSimpleDTO toSimpleDTO(Ejercicio entity);
}
