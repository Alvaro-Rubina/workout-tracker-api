package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.usuario.*;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RolMapper.class, RutinaMapper.class, EjercicioMapper.class, AgendaMapper.class, ComentarioMapper.class})
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO dto);

    @Mapping(target = "createdRoutines", ignore = true)
    @Mapping(target = "likedRoutines", ignore = true)
    @Mapping(target = "savedRoutines", ignore = true)
    @Mapping(target = "favoriteExercises", ignore = true)
    UsuarioStatsDTO toStatsDTO(Usuario entity);

    UsuarioResponseDTO toResponseDTO(Usuario entity);

}
