package org.alvarub.workouttrackerproject.persistence.dto.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolSimpleDTO {

    private Long id;

    private String name;

    private String description;
}
