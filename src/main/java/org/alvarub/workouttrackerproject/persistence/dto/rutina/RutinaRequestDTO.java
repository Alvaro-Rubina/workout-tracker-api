package org.alvarub.workouttrackerproject.persistence.dto.rutina;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.enums.Dificultad;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaRequestDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El campo isPublic es obligatorio")
    private Boolean isPublic;

    @NotNull(message = "El campo difficulty es obligatorio")
    private Dificultad difficulty;

    @NotNull(message = "El campo categoryId es obligatorio")
    @Positive(message = "El campo categoryId debe ser mayor a 0")
    private Long categoryId;

    @NotEmpty(message = "El campo sessions es obligatorio y debe tener al menos 1 elemento")
    private List<SesionRequestDTO> sessions;
}
