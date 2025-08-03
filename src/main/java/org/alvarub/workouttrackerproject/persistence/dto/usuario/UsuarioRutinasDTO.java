package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRutinasDTO {

    private Long id;

    private String name;

    private String email;

    private Long completedRoutines;

    private Set<RutinaSimpleDTO> createdRoutines;

    private Set<RutinaSimpleDTO> likedRoutines;

    private Set<RutinaSimpleDTO> savedRoutines;

}
