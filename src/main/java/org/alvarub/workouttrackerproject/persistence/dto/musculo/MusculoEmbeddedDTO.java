package org.alvarub.workouttrackerproject.persistence.dto.musculo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusculoEmbeddedDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @Builder.Default
    private Boolean active = true;
}