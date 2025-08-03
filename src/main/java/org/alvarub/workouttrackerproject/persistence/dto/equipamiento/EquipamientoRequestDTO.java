package org.alvarub.workouttrackerproject.persistence.dto.equipamiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipamientoRequestDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @NotNull(message = "El campo active es obligatorio")
    private Boolean active;

}
