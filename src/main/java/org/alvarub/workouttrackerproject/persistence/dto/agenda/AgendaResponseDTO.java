package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import lombok.*;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaResponseDTO {

    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime startDate;

    private Integer reminderMinutes;

    private String comment;

    private RutinaSimpleDTO routine;

    private UsuarioStatsDTO user;

}
