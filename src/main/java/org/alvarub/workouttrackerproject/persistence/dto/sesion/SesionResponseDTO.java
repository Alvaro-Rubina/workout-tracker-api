package org.alvarub.workouttrackerproject.persistence.dto.sesion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio.SesionEjercicioResponseDTO;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionResponseDTO {

    private Long id;

    private String name;

    private String description;

    private DayOfWeek dayOfWeek;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CategoriaResponseDTO category;

    private Set<SesionEjercicioResponseDTO> sessionExercises;
}
