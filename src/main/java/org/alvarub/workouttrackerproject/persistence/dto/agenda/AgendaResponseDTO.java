package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import lombok.*;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaResponseDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startDate;

    private Integer reminderMinutes;

    private String comment;

    private Boolean completed;

    private LocalDateTime completedAt;

    private RutinaResponseDTO routine;

    private UsuarioResponseDTO user;

}
