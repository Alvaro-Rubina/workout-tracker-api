package org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioResponseDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionEjercicioResponseDTO {

    private Long id;

    private Long sets;

    private Long reps;

    private Double restBetweenSets;

    private String comment;

    private EjercicioResponseDTO exercise;

    // NOTE: no veo muy util poner el campo como SimpleDTO en este DTO. En otros capaz sí
    /*private EjercicioSimpleDTO exercise;*/

}
