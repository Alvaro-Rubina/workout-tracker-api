package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.PesoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Peso;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.PesoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PesoService {

    private final PesoRepository pesoRepository;
    private final PesoMapper pesoMapper;
    private final UsuarioService usuarioService;

    @Transactional
    public PesoResponseDTO save(String auth0UserId, PesoRequestDTO dto) {
        Peso peso = pesoMapper.toEntity(dto);

        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        usuario.getBodyWeightHistorial().add(peso);
        peso.setUser(usuario);

        return pesoMapper.toResponseDTO(pesoRepository.save(peso));
    }

    @Transactional(readOnly = true)
    public PesoResponseDTO getUserLastBodyWeight(String auth0UserId) {
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        return usuario.getBodyWeightHistorial().stream()
                .max(Comparator.comparing(Peso::getCreatedAt))
                .map(pesoMapper::toResponseDTO)
                .orElseThrow(() -> new NotFoundException("No hay registros de peso para el usuario"));
    }

    @Transactional(readOnly = true)
    public List<PesoResponseDTO> getUserBodyWeights(String auth0UserId) {
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        return usuario.getBodyWeightHistorial().stream()
                .map(pesoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public PesoResponseDTO updateLast(String auth0UserId, PesoRequestDTO dto) {
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        List<Peso> historial = usuario.getBodyWeightHistorial();
        if (historial.isEmpty()) {
            throw new NotFoundException("No hay registros de peso para el usuario");
        }

        Peso ultimoPeso = historial.get(historial.size() - 1);
        ultimoPeso.setBodyWeight(dto.getBodyWeight());

        return pesoMapper.toResponseDTO(ultimoPeso);
    }

    // Metodos auxiliares
    public Peso getPesoOrThrow(Long id) {
        return pesoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Peso con el id " + id + " no encontrado"));
    }
}
