package org.alvarub.workouttrackerproject.persistence.dto.musculo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusculoUpdateRequestDTO {

    private String name;

    private Boolean active;

    @Positive(message = "El campo muscleGroupId debe ser mayor a 0")
    private Long muscleGroupId;
}
