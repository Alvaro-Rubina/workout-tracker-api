package org.alvarub.workouttrackerproject.persistence.dto.categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaUpdateRequestDTO {

    private String name;

    private Boolean active = true;
}
