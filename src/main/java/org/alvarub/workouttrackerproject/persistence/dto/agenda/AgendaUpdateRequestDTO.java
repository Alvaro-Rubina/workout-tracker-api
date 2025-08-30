package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaUpdateRequestDTO {

    // Todos los campos son opcionales para PATCH
    @FutureOrPresent(message = "El campo startDate debe ser una fecha futura o presente")
    private LocalDateTime startDate;

    private Integer reminderMinutes;

    @Size(max = 500, message = "El comentario no puede tener más de 500 caracteres")
    private String comment;

    @Positive(message = "El campo userId debe ser mayor a 0")
    private Long userId;

    @Positive(message = "El campo routineId debe ser mayor a 0")
    private Long routineId;
}
