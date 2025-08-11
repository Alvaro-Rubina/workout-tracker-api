package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaRutinaDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startDate;

    private Integer reminderMinutes;

    private String comment;

    private Boolean completed;

    private LocalDateTime completedAt;

    private RutinaSimpleDTO routine;
}
