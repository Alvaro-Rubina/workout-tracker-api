package org.alvarub.workouttrackerproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.BusinessException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.PesoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Peso;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.PesoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PesoService {

    private final PesoRepository pesoRepository;
    private final PesoMapper pesoMapper;
    private final UsuarioService usuarioService;

    @Transactional
    public PesoResponseDTO save(Long userId, PesoRequestDTO dto) {
        Usuario usuario = usuarioService.getUsuarioOrThrow(userId, true);
        Peso peso = pesoMapper.toEntity(dto);

        usuario.getHistorialPeso().add(peso);
        peso.setUser(usuario);

        return pesoMapper.toResponseDTO(pesoRepository.save(peso));
    }

    @Transactional
    public PesoResponseDTO update(Long userId, Long id, PesoRequestDTO dto) {
        Usuario usuario = usuarioService.getUsuarioOrThrow(userId, true);
        Peso peso = getPesoOrThrow(id);

        Peso ultimoPeso = usuario.getHistorialPeso()
            .stream()
            .max(Comparator.comparing(Peso::getCreatedAt))
            .orElseThrow(() -> new NotFoundException("No hay registros de peso para el usuario"));

        if (!ultimoPeso.getId().equals(peso.getId())) {
            throw new BusinessException("Solo se puede modificar el último peso registrado");
        }

        peso.setBodyWeight(dto.getBodyWeight());
        return pesoMapper.toResponseDTO(peso);
    }

    // Metodos auxiliares
    public Peso getPesoOrThrow(Long id) {
        return pesoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Peso con el id " + id + " no encontrado"));
    }
}
