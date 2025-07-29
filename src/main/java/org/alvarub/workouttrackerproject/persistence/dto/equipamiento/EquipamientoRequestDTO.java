package org.alvarub.workouttrackerproject.persistence.dto.equipamiento;

import jakarta.validation.constraints.NotBlank;
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

}
