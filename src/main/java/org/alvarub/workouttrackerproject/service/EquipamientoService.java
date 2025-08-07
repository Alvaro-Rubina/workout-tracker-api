package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.EquipamientoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Equipamiento;
import org.alvarub.workouttrackerproject.persistence.repository.EquipamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamientoService {

    private final EquipamientoRepository equipamientoRepository;
    private final EquipamientoMapper equipamientoMapper;

    @Transactional
    public EquipamientoResponseDTO save (EquipamientoRequestDTO requestDTO) {
        Equipamiento equipamiento = equipamientoMapper.toEntity(requestDTO);
        return equipamientoMapper.toResponseDTO(equipamientoRepository.save(equipamiento));
    }

    @Transactional(readOnly = true)
    public EquipamientoResponseDTO findById (Long id) {
        return equipamientoMapper.toResponseDTO(getEjercicioOrThrow(id, false));
    }

    @Transactional(readOnly = true)
    public List<EquipamientoResponseDTO> findAll() {
        return equipamientoRepository.findAll().stream()
                .map(equipamientoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public EquipamientoResponseDTO toggleActive(Long id) {
        Equipamiento equipamiento = getEjercicioOrThrow(id, false);
        equipamiento.setActive(!equipamiento.getActive());
        return equipamientoMapper.toResponseDTO(equipamientoRepository.save(equipamiento));
    }

    @Transactional
    public EquipamientoResponseDTO softDelete(Long id) {
        Equipamiento equipamiento = getEjercicioOrThrow(id, false);

        if (!equipamiento.getActive()) {
            return equipamientoMapper.toResponseDTO(equipamiento);
        }

        equipamiento.setActive(false);
        return equipamientoMapper.toResponseDTO(equipamientoRepository.save(equipamiento));
    }

    // Métodos auxiliares
    public Equipamiento getEjercicioOrThrow(Long id, boolean verifyActive) {
        Equipamiento equipamiento = equipamientoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipamiento con el ID " + id + " no encontrado"));

        if (verifyActive && !equipamiento.getActive()) {
            throw new NotFoundException("Equipamiento con el ID " + id + " inactivo");
        }

        return equipamiento;
    }
}
