package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolResponseDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponseDTO {

    private Long id;

    private String name;

    private String email;

    private Double bodyWeight;

    private String pictureUrl;

    private Boolean active;

    private String picture;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastAccess;

    private Long createdRoutines;

    private Long completedRoutines;

    private Long likedRoutines;

    private Long savedRoutines;

    private Long favoriteExercises;

    private RolResponseDTO role;
}
