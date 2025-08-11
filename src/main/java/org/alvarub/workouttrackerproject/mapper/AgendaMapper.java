package org.alvarub.workouttrackerproject.mapper;

import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRutinaDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Agenda;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, RutinaMapper.class})
public interface AgendaMapper {

    Agenda toEntity(AgendaRequestDTO dto);

    AgendaResponseDTO toResponseDTO(Agenda entity);

    AgendaRutinaDTO toRutinaDTO(Agenda entity);
}
