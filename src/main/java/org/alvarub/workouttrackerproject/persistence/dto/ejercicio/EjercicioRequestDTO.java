package org.alvarub.workouttrackerproject.persistence.dto.ejercicio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioRequestDTO implements Serializable {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @NotBlank(message = "El campo description es obligatorio")
    private String description;

    private String tips;

    @NotNull(message = "El campo instructions es obligatorio")
    private Map<Integer, String> instructions;

    @NotEmpty(message = "El campo sampleVideos es obligatorio y debe tener al menos 1 elemento")
    private Set<String> sampleVideos;

    private Set<Long> equipmentIds;

    @NotEmpty(message = "El campo targetMuscleIds es obligatorio y debe tener al menos 1 elemento")
    private Set<Long> targetMuscleIds;
}