package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.ZonaMuscularMapper;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.ZonaMuscular;
import org.alvarub.workouttrackerproject.persistence.repository.ZonaMuscularRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaMuscularService {

    private final ZonaMuscularRepository zonaMuscularRepository;
    private final ZonaMuscularMapper zonaMuscularMapper;

    @Transactional
    public ZonaMuscularResponseDTO save(ZonaMuscularRequestDTO dto) {
        ZonaMuscular zonaMuscular = zonaMuscularMapper.toEntity(dto);

        zonaMuscular.getMuscles().forEach(muscle -> muscle.setMuscleGroup(zonaMuscular));

        return zonaMuscularMapper.toResponseDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    @Transactional(readOnly = true)
    public ZonaMuscularResponseDTO findById(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id);
        return zonaMuscularMapper.toResponseDTO(zonaMuscular);
    }

    @Transactional(readOnly = true)
    public ZonaMuscularSimpleDTO findByIdSimple(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id);
        return zonaMuscularMapper.toSimpleDTO(zonaMuscular);
    }

    @Transactional(readOnly = true)
    public List<ZonaMuscularResponseDTO> findAll() {
        return zonaMuscularRepository.findAll().stream()
                .map(zonaMuscularMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ZonaMuscularSimpleDTO> findAllSimple() {
        return zonaMuscularRepository.findAll().stream()
                .map(zonaMuscularMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public ZonaMuscularSimpleDTO toggleActive(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id);
        zonaMuscular.setActive(!zonaMuscular.getActive());
        return zonaMuscularMapper.toSimpleDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    @Transactional
    public ZonaMuscularSimpleDTO softDelete(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id);

        if (!zonaMuscular.getActive()) {
            return zonaMuscularMapper.toSimpleDTO(zonaMuscular);
        }

        zonaMuscular.setActive(false);
        return zonaMuscularMapper.toSimpleDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    // Métodos auxiliares
    public ZonaMuscular getZonaMuscularOrThrow(Long id) {
        return zonaMuscularRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zona Muscular con el ID " + id + " no encontrada"));
    }

}
