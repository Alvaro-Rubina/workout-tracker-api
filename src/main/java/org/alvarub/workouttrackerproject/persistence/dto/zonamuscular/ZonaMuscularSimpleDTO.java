package org.alvarub.workouttrackerproject.persistence.dto.zonamuscular;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaMuscularSimpleDTO {

    private Long id;

    private String name;
}
