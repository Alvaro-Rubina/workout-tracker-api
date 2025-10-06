package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.BusinessException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.EjercicioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Ejercicio;
import org.alvarub.workouttrackerproject.persistence.entity.Musculo;
import org.alvarub.workouttrackerproject.persistence.repository.EjercicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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

        if (dto.getEquipmentIds() != null && !dto.getEquipmentIds().isEmpty()) {
            dto.getEquipmentIds().forEach(equipmentId ->
                    ejercicio.getEquipment().add(equipamientoService.getEquipamientoOrThrow(equipmentId, true))
            );
        }

        dto.getTargetMuscleIds().forEach(targetMuscleId ->
                ejercicio.getTargetMuscles().add(musculoService.getMusculoOrThrow(targetMuscleId, true))
        );

        return ejercicioMapper.toResponseDTO(ejercicioRepository.save(ejercicio));
    }

    @Transactional(readOnly = true)
    public EjercicioResponseDTO findById(Long id, boolean verifyActive) {
        Ejercicio ejercicio = getEjercicioOrThrow(id, verifyActive);
        return ejercicioMapper.toResponseDTO(ejercicio);
    }

    @Transactional(readOnly = true)
    public EjercicioSimpleDTO findByIdSimple(Long id, boolean verifyActive) {
        Ejercicio ejercicio = getEjercicioOrThrow(id, verifyActive);
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

    @Transactional(readOnly = true)
    public List<EjercicioResponseDTO> findAllActive() {
        return ejercicioRepository.findAll().stream()
                .filter(Ejercicio::getActive)
                .map(ejercicioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EjercicioSimpleDTO> findAllSimpleActive() {
        return ejercicioRepository.findAll().stream()
                .filter(Ejercicio::getActive)
                .map(ejercicioMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public EjercicioSimpleDTO toggleActive(Long id) {
        Ejercicio ejercicio = getEjercicioOrThrow(id, false);

        if (!ejercicio.getActive() && ejercicio.getTargetMuscles().stream().noneMatch(Musculo::getActive)) {
            throw new BusinessException("Debe haber por lo menos 1 músculo objetivo activo para activar el músculo");
        }

        ejercicio.setActive(!ejercicio.getActive());

        return ejercicioMapper.toSimpleDTO(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public EjercicioSimpleDTO softDelete(Long id) {
        Ejercicio ejercicio = getEjercicioOrThrow(id, false);

        if (!ejercicio.getActive()) {
            return ejercicioMapper.toSimpleDTO(ejercicio);
        }

        ejercicio.setActive(false);
        return ejercicioMapper.toSimpleDTO(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public EjercicioResponseDTO update(Long id, EjercicioUpdateRequestDTO dto) {
        Ejercicio ejercicio = getEjercicioOrThrow(id, false);

        if (dto.getName() != null) {
            ejercicio.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            ejercicio.setDescription(dto.getDescription());
        }

        if (dto.getActive() != null) {
            ejercicio.setActive(dto.getActive());
        }

        if (dto.getTips() != null) {
            ejercicio.setTips(dto.getTips());
        }

        if (dto.getInstructions() != null) {
            ejercicio.setInstructions(new LinkedHashMap<>(dto.getInstructions()));
        }

        if (dto.getSampleVideos() != null) {
            ejercicio.setSampleVideos(new HashSet<>(dto.getSampleVideos()));
        }

        if (dto.getEquipmentIds() != null) {
            ejercicio.getEquipment().clear();
            dto.getEquipmentIds().forEach(equipmentId ->
                    ejercicio.getEquipment().add(equipamientoService.getEquipamientoOrThrow(equipmentId, true))
            );
        }

        if (dto.getTargetMuscleIds() != null) {
            ejercicio.getTargetMuscles().clear();
            dto.getTargetMuscleIds().forEach(targetMuscleId ->
                    ejercicio.getTargetMuscles().add(musculoService.getMusculoOrThrow(targetMuscleId, true))
            );
        }

        return ejercicioMapper.toResponseDTO(ejercicioRepository.save(ejercicio));
    }

    // Métodos auxiliares
    @Transactional(readOnly = true)
    public Ejercicio getEjercicioOrThrow(Long id, boolean verifyActive) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ejercicio con el ID " + id + " no encontrado"));

        if (verifyActive && !ejercicio.getActive()) {
            throw new NotFoundException("Ejercicio con el ID " + id + " inactivo");
        }

        return ejercicio;
    }
}
