package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaRequestDTO {

    @NotNull(message = "El campo startDate es obligatorio")
    @FutureOrPresent(message = "El campo startDate debe ser una fecha futura o presente")
    private LocalDateTime startDate;

    // Campos reminderMinutes y comment son opcional
    private Integer reminderMinutes;

    private String comment;

    @NotNull(message = "El campo userId es obligatorio")
    @Positive(message = "El campo userId debe ser mayor a 0")
    private Long userId;

    @NotNull(message = "El campo userId es obligatorio")
    @Positive(message = "El campo routineId debe ser mayor a 0")
    private Long routineId;
}
