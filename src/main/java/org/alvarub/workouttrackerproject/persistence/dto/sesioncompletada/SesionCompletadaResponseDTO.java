package org.alvarub.workouttrackerproject.persistence.dto.sesioncompletada;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionCompletadaResponseDTO {

    private Long id;

    private UsuarioStatsDTO user;

    private RutinaSimpleDTO session;

    private LocalDate sessionDate;

    private LocalDate registrationDate;

}
