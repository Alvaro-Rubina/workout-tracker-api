package org.alvarub.workouttrackerproject.persistence.dto.peso;

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
public class PesoRequestDTO {

    @NotNull(message = "El campo bodyWeight es obligatorio")
    @Positive(message = "El campo bodyWeight debe ser mayor a 0")
    private Double bodyWeight;
}
