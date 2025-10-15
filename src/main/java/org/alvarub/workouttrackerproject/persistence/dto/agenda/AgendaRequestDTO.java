package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaRequestDTO {


    // Campos reminderMinutes y comment son opcional
    private Integer reminderMinutes;

    @Size(max = 500, message = "El comentario no puede tener más de 500 caracteres")
    private String comment;

    @NotNull(message = "El campo userId es obligatorio")
    @Positive(message = "El campo routineId debe ser mayor a 0")
    private Long routineId;
}