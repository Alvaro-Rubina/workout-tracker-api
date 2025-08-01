package org.alvarub.workouttrackerproject.persistence.dto.musculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusculoResponseDTO {

    private Long id;

    private String name;

    private ZonaMuscularSimpleDTO zonaMuscular;
}
