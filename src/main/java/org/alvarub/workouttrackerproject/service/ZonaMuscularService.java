package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.ZonaMuscularMapper;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Equipamiento;
import org.alvarub.workouttrackerproject.persistence.entity.ZonaMuscular;
import org.alvarub.workouttrackerproject.persistence.repository.ZonaMuscularRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaMuscularService {

    private final ZonaMuscularRepository zonaMuscularRepository;
    private final ZonaMuscularMapper zonaMuscularMapper;

    public ZonaMuscularResponseDTO save(ZonaMuscularRequestDTO dto) {
        ZonaMuscular zonaMuscular = zonaMuscularMapper.toEntity(dto);

        zonaMuscular.getMuscles().forEach(muscle -> muscle.setMuscleGroup(zonaMuscular));

        return zonaMuscularMapper.toResponseDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    public ZonaMuscularResponseDTO findById(Long id) {
        ZonaMuscular zonaMuscular = findZonaMuscularById(id);
        return zonaMuscularMapper.toResponseDTO(zonaMuscular);
    }

    public ZonaMuscularSimpleDTO findByIdSimple(Long id) {
        ZonaMuscular zonaMuscular = findZonaMuscularById(id);
        return zonaMuscularMapper.toSimpleDTO(zonaMuscular);
    }

    public List<ZonaMuscularResponseDTO> findAll() {
        return zonaMuscularRepository.findAll().stream()
                .map(zonaMuscularMapper::toResponseDTO)
                .toList();
    }

    public List<ZonaMuscularSimpleDTO> findAllSimple() {
        return zonaMuscularRepository.findAll().stream()
                .map(zonaMuscularMapper::toSimpleDTO)
                .toList();
    }

    public ZonaMuscularResponseDTO toggleActive(Long id) {
        ZonaMuscular zonaMuscular = findZonaMuscularById(id);
        zonaMuscular.setActive(!zonaMuscular.getActive());
        return zonaMuscularMapper.toResponseDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    public ZonaMuscularResponseDTO softDelete(Long id) {
        ZonaMuscular zonaMuscular = findZonaMuscularById(id);
        zonaMuscular.setActive(false);
        return zonaMuscularMapper.toResponseDTO(zonaMuscularRepository.save(zonaMuscular));
    }


    // Métodos auxiliares
    public ZonaMuscular findZonaMuscularById(Long id) {
        return zonaMuscularRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zona Muscular con el ID " + id + " no encontrada"));
    }

}
