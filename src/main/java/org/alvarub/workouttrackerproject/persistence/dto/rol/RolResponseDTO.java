package org.alvarub.workouttrackerproject.persistence.dto.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Set<UsuarioStatsDTO> users;
}
