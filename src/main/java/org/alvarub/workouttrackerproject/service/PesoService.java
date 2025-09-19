package org.alvarub.workouttrackerproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.mapper.PesoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Peso;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.PesoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PesoService {

    private final PesoRepository pesoRepository;
    private final PesoMapper pesoMapper;
    private final UsuarioService usuarioService;

    @Transactional
    public PesoResponseDTO save(PesoRequestDTO dto) {
        Peso peso = pesoMapper.toEntity(dto);
        Usuario usuario = usuarioService.getUsuarioOrThrow(dto.getUserId(), true);

        usuario.getPesosHistoricos().add(peso);
        peso.setUser(usuario);

        return pesoMapper.toResponseDTO(pesoRepository.save(peso));
    }
}
