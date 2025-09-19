package org.alvarub.workouttrackerproject.persistence.dto.peso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PesoResponseDTO {

    private Long id;

    private Double bodyWeight;

    private Long userId;
}
