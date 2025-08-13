package org.alvarub.workouttrackerproject.persistence.dto.comentario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioResponseDTO {

    private Long id;

    private String content;

    private Long likes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UsuarioResponseDTO user;

    private RutinaSimpleDTO routine;

    private ComentarioSimpleDTO replyTo;

    // NOTE: Acá no estoy seguro de si ponerlo de tipo ResponseDTO o SimpleDTO.
    // En ComentarioResponseDTO
    private List<ComentarioResponseDTO> replies;
}
