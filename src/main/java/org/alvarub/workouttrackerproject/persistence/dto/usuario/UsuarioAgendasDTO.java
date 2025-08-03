package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRutinaDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAgendasDTO {

    private Long id;

    private String name;

    private String email;

    private Long completedRoutines;

    private List<AgendaRutinaDTO> schedules;
}
