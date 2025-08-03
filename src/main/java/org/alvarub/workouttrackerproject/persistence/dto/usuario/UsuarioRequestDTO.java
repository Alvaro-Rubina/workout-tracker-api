package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @NotBlank(message = "El campo email es obligatorio")
    private String email;

    // TODO: Este campo está comentado en la entidad, en todo caso no se si se tenga que mandar en el DTO
    /*private String auth0Id;*/

    private Boolean active;

    private Set<Long> roleIds;
}
