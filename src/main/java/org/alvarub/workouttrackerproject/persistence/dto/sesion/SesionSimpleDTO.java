package org.alvarub.workouttrackerproject.persistence.dto.sesion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionSimpleDTO {

    private Long id;

    private String name;

    private String description;

    private DayOfWeek dayOfWeek;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
