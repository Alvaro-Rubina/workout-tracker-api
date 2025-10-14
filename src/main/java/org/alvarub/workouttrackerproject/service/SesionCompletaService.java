package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.mapper.SesionCompletadaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.sesioncompletada.SesionCompletadaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.SesionCompletadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionCompletaService {

    private final SesionCompletadaRepository sesionCompletadaRepository;
    private final SesionCompletadaMapper sesionCompletadaMapper;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<SesionCompletadaResponseDTO> findAll(String auth0UserId) {
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);
        return sesionCompletadaRepository.findByUser_Id(usuario.getId()).stream()
                .map(sesionCompletadaMapper::toResponseDTO)
                .toList();
    }
}
