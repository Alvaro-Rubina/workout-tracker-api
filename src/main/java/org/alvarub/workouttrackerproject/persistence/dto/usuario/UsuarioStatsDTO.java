package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolSimpleDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioStatsDTO implements Serializable {

    private Long id;

    private String name;

    private String email;

    private Long bodyWeight;

    private Long completedWorkouts;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastAccess;

    private Long createdRoutines;

    private Long likedRoutines;

    private Long savedRoutines;

    private Long completedRoutines;

    private Long favoriteExercises;

    // Tema agendas y comentarios no se si sea relevante poner

    private Set<RolSimpleDTO> roles;
}