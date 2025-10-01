package org.alvarub.workouttrackerproject.persistence.dto.rol;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolUpdateRequestDTO {

    @Size(max = 50, message = "El campo name no puede tener mas de 50 caracteres")
    private String name;

    @Size(max = 500, message = "El campo description no puede tener mas de 500 caracteres")
    private String description;

    private Boolean active;
}
