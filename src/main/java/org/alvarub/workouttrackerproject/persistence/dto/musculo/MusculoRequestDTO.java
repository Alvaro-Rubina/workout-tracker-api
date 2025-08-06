package org.alvarub.workouttrackerproject.persistence.dto.musculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusculoRequestDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    private Boolean active;

    @NotNull(message = "El campo muscleGroupId es obligatorio")
    @Positive(message = "El campo muscleGroupId debe ser mayor a 0")
    private Long muscleGroupId;
}
