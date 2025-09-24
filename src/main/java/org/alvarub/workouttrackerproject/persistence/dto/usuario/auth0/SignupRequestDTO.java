package org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

    @NotBlank(message = "El campo email es obligatorio")
    private String email;

    @NotBlank(message = "El campo password es obligatorio")
    private String password;

    private String name;
}
