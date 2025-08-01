package org.alvarub.workouttrackerproject.persistence.dto.ejercicio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoSimpleDTO;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String tips;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Map<Integer, String> instructions;

    private Set<String> sampleVideos;

    private Set<EquipamientoResponseDTO> equipment;

    private Set<MusculoSimpleDTO> targetMuscles;

}
