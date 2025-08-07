package org.alvarub.workouttrackerproject.persistence.dto.zonamuscular;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoSimpleDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaMuscularResponseDTO {

    private Long id;

    private String name;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<MusculoSimpleDTO> muscles;
}
