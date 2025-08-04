package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoriaMapper.class, SesionMapper.class})
public interface RutinaMapper {

    Rutina toEntity(RutinaRequestDTO dto);

    @Mapping(target = "user.createdRoutines", ignore = true)
    @Mapping(target = "user.savedRoutines", ignore = true)
    @Mapping(target = "user.likedRoutines", ignore = true)
    @Mapping(target = "user.favoriteExercises", ignore = true)
    RutinaResponseDTO toResponseDTO(Rutina entity);

    RutinaSimpleDTO toSimpleDTO(Rutina entity);
}
