package org.alvarub.workouttrackerproject.persistence.dto.agenda;

import jakarta.validation.constraints.Size;

public record AgendaCompleteRequestDTO(
        @Size(max = 500, message = "El comentario no puede tener más de 500 caracteres")
        String comment) {
}
