package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.BusinessException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.MusculoMapper;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Musculo;
import org.alvarub.workouttrackerproject.persistence.repository.EjercicioRepository;
import org.alvarub.workouttrackerproject.persistence.repository.MusculoRepository;
import org.alvarub.workouttrackerproject.service.storage.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusculoService {

    private final MusculoRepository musculoRepository;
    private final MusculoMapper musculoMapper;
    private final ZonaMuscularService zonaMuscularService;
    private final EjercicioRepository ejercicioRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public MusculoResponseDTO save(MusculoRequestDTO dto, MultipartFile image) {
        Musculo musculo = musculoMapper.toEntity(dto);
        musculo.setMuscleGroup(zonaMuscularService.getZonaMuscularOrThrow(dto.getMuscleGroupId(), true));

        if (image != null && !image.isEmpty()) {
            var res = cloudinaryService.upload(image, "muscles");
            if (res != null) {
                musculo.setImageUrl(res.url());
                musculo.setImagePublicId(res.publicId());
            }
        }

        return musculoMapper.toResponseDTO(musculoRepository.save(musculo));
    }

    @Transactional(readOnly = true)
    public MusculoResponseDTO findById(Long id, boolean verifyActive) {
        Musculo musculo = getMusculoOrThrow(id, verifyActive);
        return musculoMapper.toResponseDTO(musculo);
    }

    @Transactional(readOnly = true)
    public MusculoSimpleDTO findByIdSimple(Long id, boolean verifyActive) {
        Musculo musculo = getMusculoOrThrow(id, verifyActive);
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

    @Transactional(readOnly = true)
    public List<MusculoResponseDTO> findAllActive() {
        return musculoRepository.findAll().stream()
                .filter(Musculo::getActive)
                .map(musculoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MusculoSimpleDTO> findAllSimpleActive() {
        return musculoRepository.findAll().stream()
                .filter(Musculo::getActive)
                .map(musculoMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public MusculoSimpleDTO toggleActive(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);

        boolean active = !musculo.getActive();
        if (active) {
            if (!musculo.getMuscleGroup().getActive()) {
                throw new BusinessException("No es posible activar un músculo cuya zona muscular está inactiva");
            }
        } else {
            deactivateRelatedEjercicios(musculo);
        }

        musculo.setActive(active);
        return musculoMapper.toSimpleDTO(musculoRepository.save(musculo));
    }

    @Transactional
    public MusculoResponseDTO update(Long id, MusculoUpdateRequestDTO dto, MultipartFile image) {
        Musculo musculo = getMusculoOrThrow(id, false);

        if ((dto.getName() != null && !dto.getName().isBlank()) && (!musculo.getName().equalsIgnoreCase(dto.getName()))) {
            musculo.setName(dto.getName());
        }

        if ((dto.getActive() != null) && (!musculo.getActive().equals(dto.getActive()))) {
            musculo.setActive(dto.getActive());
            if (!musculo.getActive()) {
                deactivateRelatedEjercicios(musculo);
            }
        }

        if (dto.getMuscleGroupId() != null) {
            musculo.setMuscleGroup(zonaMuscularService.getZonaMuscularOrThrow(dto.getMuscleGroupId(), true));
        }

        if (image != null) {
            // si envían image vacía => ignoramos; si mandan nueva => reemplazamos
            if (!image.isEmpty()) {
                // borra la anterior si existe
                cloudinaryService.delete(musculo.getImagePublicId());
                var res = cloudinaryService.upload(image, "muscles");
                if (res != null) {
                    musculo.setImageUrl(res.url());
                    musculo.setImagePublicId(res.publicId());
                }
            }
        }

        return musculoMapper.toResponseDTO(musculo);
    }

    @Transactional
    public MusculoResponseDTO removeImage(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);
        cloudinaryService.delete(musculo.getImagePublicId());
        musculo.setImagePublicId(null);
        musculo.setImageUrl(null);
        return musculoMapper.toResponseDTO(musculoRepository.save(musculo));
    }

    @Transactional
    public MusculoSimpleDTO softDelete(Long id) {
        Musculo musculo = getMusculoOrThrow(id, false);

        deactivateRelatedEjercicios(musculo);

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

    @Transactional
    public void deactivateRelatedEjercicios(Musculo musculo) {
        ejercicioRepository.findAllByTargetMusclesContains(musculo).stream()
                .filter(e -> e.getTargetMuscles().size() == 1 && e.getTargetMuscles().contains(musculo))
                .forEach(ejercicio -> {
                    if (ejercicio.getActive()) {
                        ejercicio.setActive(false);
                        ejercicioRepository.save(ejercicio);
                    }
                });
    }

}
