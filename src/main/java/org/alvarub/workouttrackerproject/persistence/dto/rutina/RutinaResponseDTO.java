package org.alvarub.workouttrackerproject.persistence.dto.rutina;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.persistence.enums.Dificultad;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Boolean isPublic;

    private Long likesCount;

    private Dificultad difficulty;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CategoriaResponseDTO category;

    private List<SesionResponseDTO> sessions;

    private UsuarioStatsDTO user; // NOTE: Esto podría cambiar a UsuarioSimpleDTO. Después veo
}
