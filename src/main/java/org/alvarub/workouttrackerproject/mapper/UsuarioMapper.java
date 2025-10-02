package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.usuario.*;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = {RolMapper.class, RutinaMapper.class, EjercicioMapper.class, AgendaMapper.class, ComentarioMapper.class, PesoMapper.class })
public interface UsuarioMapper {

    UsuarioStatsDTO toStatsDTO(Usuario entity);

    UsuarioResponseDTO toResponseDTO(Usuario entity);

    default Long size(Collection<?> c) {
        return c == null ? 0L : (long) c.size();
    }

}
