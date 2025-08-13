package org.alvarub.workouttrackerproject.persistence.dto.comentario;

import jakarta.validation.constraints.NotBlank;

public record ComentarioContentRequestDTO (
        @NotBlank(message = "El campo content es obligatorio")
        String content){
}
