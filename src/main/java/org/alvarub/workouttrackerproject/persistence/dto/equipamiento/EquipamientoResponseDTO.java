package org.alvarub.workouttrackerproject.persistence.dto.equipamiento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipamientoResponseDTO {

    private Long id;

    private String name;

    private Boolean active;
}
