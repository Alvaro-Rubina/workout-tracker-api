package org.alvarub.workouttrackerproject.persistence.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioRequestDTO {

    @NotBlank(message = "El campo content es obligatorio")
    private String content;

    @NotNull(message = "El campo userId es obligatorio")
    @Positive(message = "El campo userId debe ser mayor a 0")
    private Long userId;

    @NotNull(message = "El campo userId es obligatorio")
    @Positive(message = "El campo routineId debe ser mayor a 0")
    private Long routineId;

    private Long replyToId;
}
