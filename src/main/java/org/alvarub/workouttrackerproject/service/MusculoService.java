package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.MusculoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Musculo;
import org.alvarub.workouttrackerproject.persistence.entity.ZonaMuscular;
import org.alvarub.workouttrackerproject.persistence.repository.MusculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusculoService {

    private final MusculoRepository musculoRepository;
    private final MusculoMapper musculoMapper;
    private final ZonaMuscularService zonaMuscularService;

    @Transactional
    public MusculoResponseDTO save(MusculoRequestDTO dto) {
        Musculo musculo = musculoMapper.toEntity(dto);

        musculo.setMuscleGroup(zonaMuscularService.getZonaMuscularOrThrow(dto.getMuscleGroupId(), true));

        return musculoMapper.toResponseDTO(musculoRepository.save(musculo));
    }

    @Transactional(readOnly = true)
    public MusculoResponseDTO findById(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);
        return musculoMapper.toResponseDTO(musculo);
    }

    @Transactional(readOnly = true)
    public MusculoSimpleDTO findByIdSimple(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);
        return musculoMapper.toSimpleDTO(musculo);
    }

    @Transactional(readOnly = true)
    public List<MusculoResponseDTO> findAll() {
        return musculoRepository.findAll().stream()
                .map(musculoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MusculoSimpleDTO> findAllSimple() {
        return musculoRepository.findAll().stream()
                .map(musculoMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public MusculoSimpleDTO toggleActive(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);
        musculo.setActive(!musculo.getActive());
        return musculoMapper.toSimpleDTO(musculoRepository.save(musculo));
    }

    @Transactional
    public MusculoSimpleDTO softDelete(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);

        if (!musculo.getActive()) {
            return musculoMapper.toSimpleDTO(musculo);
        }

        musculo.setActive(false);
        return musculoMapper.toSimpleDTO(musculoRepository.save(musculo));
    }

    // Métodos auxiliares
    public Musculo getMusculoOrThrow(Long id, boolean verifyActive) {
        Musculo musculo = musculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Músculo con el ID " + id + " no encontrado"));

        if (verifyActive && !musculo.getActive()) {
            throw new NotFoundException("Músculo con el ID " + id + " inactivo");
        }

        return musculo;
    }

}
