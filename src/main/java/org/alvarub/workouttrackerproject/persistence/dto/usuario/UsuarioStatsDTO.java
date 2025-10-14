package org.alvarub.workouttrackerproject.persistence.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioStatsDTO implements Serializable {

    private Long id;

    private String name;

    private String email;

    private String pictureUrl;

    private Long createdRoutines;

    private Long completedRoutines;

    private Long likedRoutines;

    private Long savedRoutines;

    private Long favoriteExercises;

    // Tema agendas y comentarios no se si sea relevante poner
}