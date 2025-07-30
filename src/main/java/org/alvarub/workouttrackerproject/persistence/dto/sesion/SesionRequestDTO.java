package org.alvarub.workouttrackerproject.persistence.dto.sesion;

import jakarta.validation.constraints.*;
import lombok.*;
import org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio.SesionEjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Sesion;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionRequestDTO implements Serializable {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @NotBlank(message = "El campo description es obligatorio")
    private String description;

    @NotNull(message = "El campo dayOfWeek es obligatorio")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "El campo categoryId es obligatorio")
    @Positive(message = "El campo categoryId debe ser mayor a 0")
    private Long categoryId;

    @NotEmpty(message = "El campo sessionExercises es obligatorio y debe tener al menos 1 elemento")
    private Set<SesionEjercicioRequestDTO> sessionExercises;
}