package org.alvarub.workouttrackerproject.persistence.dto.ejercicio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioSimpleDTO {

    private Long id;

    private String name;

    private String description;

    private Boolean active;

    private String tips;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Map<Integer, String> instructions;

    private Set<String> sampleVideos;
}
