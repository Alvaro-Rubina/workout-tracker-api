package org.alvarub.workouttrackerproject.persistence.dto.rol;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolRequestDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @NotBlank(message = "El campo description es obligatorio")
    private String description;

    @Builder.Default
    private Boolean active = true;
}
