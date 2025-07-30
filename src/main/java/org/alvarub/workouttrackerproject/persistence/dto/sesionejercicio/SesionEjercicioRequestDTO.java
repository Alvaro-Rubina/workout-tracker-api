package org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionEjercicioRequestDTO implements Serializable {

    @NotNull(message = "El campo sets es obligatorio")
    @Positive(message = "El campo sets debe ser mayor a 0")
    private Long sets;

    @NotNull(message = "El campo reps es obligatorio")
    @Positive(message = "El campo reps debe ser mayor a 0")
    private Long reps;

    @Positive(message = "El campo restBetweenSets debe ser mayor a 0")
    private Double restBetweenSets;

    private String comment;

    @NotNull(message = "El campo exerciseId es obligatorio")
    @Positive(message = "El campo exerciseId debe ser mayor a 0")
    private Long exerciseId;
}