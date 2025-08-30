package org.alvarub.workouttrackerproject.persistence.dto.rutina;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.enums.Dificultad;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaUpdateRequestDTO {

    private String name;

    private String description;

    private Boolean isPublic;

    private Dificultad difficulty;

    private Long categoryId;

    private List<SesionRequestDTO> sessions;

}
