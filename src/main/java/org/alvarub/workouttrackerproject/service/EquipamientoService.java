package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.EquipamientoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Equipamiento;
import org.alvarub.workouttrackerproject.persistence.repository.EquipamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamientoService {

    private final EquipamientoRepository equipamientoRepository;
    private final EquipamientoMapper equipamientoMapper;

    public EquipamientoResponseDTO save (EquipamientoRequestDTO requestDTO) {
        Equipamiento equipamiento = equipamientoMapper.toEntity(requestDTO);
        return equipamientoMapper.toResponseDTO(equipamientoRepository.save(equipamiento));
    }

    public EquipamientoResponseDTO findById (Long id) {
        return equipamientoMapper.toResponseDTO(findEquipamientoById(id));
    }

    public List<EquipamientoResponseDTO> findAll() {
        return equipamientoRepository.findAll().stream()
                .map(equipamientoMapper::toResponseDTO)
                .toList();
    }

    public EquipamientoResponseDTO toggleActive(Long id) {
        Equipamiento equipamiento = findEquipamientoById(id);
        equipamiento.setActive(!equipamiento.getActive());
        return equipamientoMapper.toResponseDTO(equipamientoRepository.save(equipamiento));
    }

    public EquipamientoResponseDTO softDelete(Long id) {
        Equipamiento equipamiento = findEquipamientoById(id);
        equipamiento.setActive(false);
        return equipamientoMapper.toResponseDTO(equipamientoRepository.save(equipamiento));
    }

    // Métodos auxiliares
    public Equipamiento findEquipamientoById (Long id) {
        return equipamientoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipamiento con el ID " + id + " no encontrado"));
    }
}
