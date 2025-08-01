package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Comentario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, RutinaMapper.class})
public interface ComentarioMapper {

    Comentario toEntity(ComentarioRequestDTO dt);

    ComentarioResponseDTO toResponseDTO(Comentario entity);

    ComentarioSimpleDTO toSimpleDTO(Comentario entity);
}
