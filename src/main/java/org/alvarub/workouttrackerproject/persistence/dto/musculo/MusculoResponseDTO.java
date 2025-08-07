package org.alvarub.workouttrackerproject.persistence.dto.musculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusculoResponseDTO {

    private Long id;

    private String name;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ZonaMuscularSimpleDTO muscleGroup;
}
