package org.alvarub.workouttrackerproject.persistence.dto.ejercicio;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioUpdateRequestDTO implements Serializable {

    // Todos los campos son opcionales para PATCH
    private String name;

    private String description;

    private Boolean active;

    @Size(max = 300, message = "El campo tips no puede superar 300 caracteres")
    private String tips;

    private Map<Integer, String> instructions;

    private Set<String> sampleVideos;

    private Set<Long> equipmentIds;

    private Set<Long> targetMuscleIds;
}

