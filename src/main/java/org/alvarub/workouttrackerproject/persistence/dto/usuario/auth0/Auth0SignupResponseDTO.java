package org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth0SignupResponseDTO {

    private String userId;

    private String email;

    private String name;
}
