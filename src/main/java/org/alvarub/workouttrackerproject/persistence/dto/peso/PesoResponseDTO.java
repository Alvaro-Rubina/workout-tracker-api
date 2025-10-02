package org.alvarub.workouttrackerproject.persistence.dto.peso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PesoResponseDTO {

    private Long id;

    private Double bodyWeight;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
