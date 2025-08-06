package org.alvarub.workouttrackerproject.persistence.dto.zonamuscular;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoRequestDTO;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaMuscularRequestDTO {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    private Boolean active;

    private Set<MusculoRequestDTO> muscles;

}
