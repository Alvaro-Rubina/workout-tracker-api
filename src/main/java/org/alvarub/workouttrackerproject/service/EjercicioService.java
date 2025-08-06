package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.EjercicioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Ejercicio;
import org.alvarub.workouttrackerproject.persistence.repository.EjercicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final EjercicioMapper ejercicioMapper;
    private final EquipamientoService equipamientoService;
    private final MusculoService musculoService;

    @Transactional
    public EjercicioResponseDTO save(EjercicioRequestDTO dto) {
        Ejercicio ejercicio = ejercicioMapper.toEntity(dto);

        dto.getEquipmentIds().forEach(equipmentId ->
            ejercicio.getEquipment().add(equipamientoService.findEquipamientoById(equipmentId))
        );

        dto.getTargetMuscleIds().forEach(targetMuscleId ->
                ejercicio.getTargetMuscles().add(musculoService.findMusculoById(targetMuscleId))
        );

        return ejercicioMapper.toResponseDTO(ejercicioRepository.save(ejercicio));
    }

    @Transactional(readOnly = true)
    public EjercicioResponseDTO findById(Long id) {
        Ejercicio ejercicio = findEjercicioById(id);
        return ejercicioMapper.toResponseDTO(ejercicio);
    }

    @Transactional(readOnly = true)
    public EjercicioSimpleDTO findByIdSimple(Long id) {
        Ejercicio ejercicio = findEjercicioById(id);
        return ejercicioMapper.toSimpleDTO(ejercicio);
    }

    @Transactional(readOnly = true)
    public List<EjercicioResponseDTO> findAll() {
        return ejercicioRepository.findAll().stream()
                .map(ejercicioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EjercicioSimpleDTO> findAllSimple() {
        return ejercicioRepository.findAll().stream()
                .map(ejercicioMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public EjercicioSimpleDTO toggleActive(Long id) {
        Ejercicio ejercicio = findEjercicioById(id);
        ejercicio.setActive(!ejercicio.getActive());
        return ejercicioMapper.toSimpleDTO(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public EjercicioSimpleDTO softDelete(Long id) {
        Ejercicio ejercicio = findEjercicioById(id);
        ejercicio.setActive(false);
        return ejercicioMapper.toSimpleDTO(ejercicioRepository.save(ejercicio));
    }

    // Métodos auxiliares
    public Ejercicio findEjercicioById(Long id) {
        return ejercicioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ejercicio con el ID " + id + " no encontrado"));
    }
}
