package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolResponseDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;

    private String name;

    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long bodyWeight;

    private Long completedWorkouts;

    private Boolean active;

    private LocalDateTime lastAccess;

    private Set<RolResponseDTO> roles;

}
