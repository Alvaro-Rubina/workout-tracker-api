package org.alvarub.workouttrackerproject.persistence.dto.ejercicio;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El campo sampleVideos es obligatorio")
    private Set<String> sampleVideos;

    private Set<Integer> equipmentIds;

    private Set<Integer> targetMuscleIds;
}