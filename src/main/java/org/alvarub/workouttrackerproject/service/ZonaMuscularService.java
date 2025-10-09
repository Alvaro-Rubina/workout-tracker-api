package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.ZonaMuscularMapper;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Musculo;
import org.alvarub.workouttrackerproject.persistence.entity.ZonaMuscular;
import org.alvarub.workouttrackerproject.persistence.repository.EjercicioRepository;
import org.alvarub.workouttrackerproject.persistence.repository.ZonaMuscularRepository;
import org.alvarub.workouttrackerproject.service.storage.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaMuscularService {

    private final ZonaMuscularRepository zonaMuscularRepository;
    private final ZonaMuscularMapper zonaMuscularMapper;
    private final EjercicioRepository ejercicioRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ZonaMuscularResponseDTO save(ZonaMuscularRequestDTO dto, MultipartFile image) {
        ZonaMuscular zonaMuscular = zonaMuscularMapper.toEntity(dto);

        if (dto.getMuscles() != null && !dto.getMuscles().isEmpty()) {
            zonaMuscular.getMuscles().forEach(muscle -> muscle.setMuscleGroup(zonaMuscular));
        }

        if (image != null && !image.isEmpty()) {
            var res = cloudinaryService.upload(image, "muscle-groups");
            if (res != null) {
                zonaMuscular.setImageUrl(res.url());
                zonaMuscular.setImagePublicId(res.publicId());
            }
        }

        return zonaMuscularMapper.toResponseDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    @Transactional(readOnly = true)
    public ZonaMuscularResponseDTO findById(Long id, boolean verifyActive) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id, verifyActive);
        return zonaMuscularMapper.toResponseDTO(zonaMuscular);
    }

    @Transactional(readOnly = true)
    public ZonaMuscularSimpleDTO findByIdSimple(Long id, boolean verifyActive) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id, verifyActive);
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

    @Transactional(readOnly = true)
    public List<ZonaMuscularResponseDTO> findAllActive() {
        return zonaMuscularRepository.findAll().stream()
                .filter(ZonaMuscular::getActive)
                .map(zonaMuscularMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ZonaMuscularSimpleDTO> findAllSimpleActive() {
        return zonaMuscularRepository.findAll().stream()
                .filter(ZonaMuscular::getActive)
                .map(zonaMuscularMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public ZonaMuscularSimpleDTO toggleActive(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id, false);

        zonaMuscular.setActive(!zonaMuscular.getActive());
        zonaMuscular.getMuscles().forEach(m -> {
            m.setActive(zonaMuscular.getActive());
            if (!m.getActive()) {
                deactivateRelatedEjercicios(m);
            }
        });

        return zonaMuscularMapper.toSimpleDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    @Transactional
    public ZonaMuscularSimpleDTO softDelete(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id, false);

        if (!zonaMuscular.getActive()) {
            return zonaMuscularMapper.toSimpleDTO(zonaMuscular);
        }

        zonaMuscular.setActive(false);
        zonaMuscular.getMuscles().forEach(m -> {
            m.setActive(false);
            deactivateRelatedEjercicios(m);
        });

        return zonaMuscularMapper.toSimpleDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    @Transactional
    public ZonaMuscularResponseDTO update(Long id, ZonaMuscularUpdateRequestDTO dto, MultipartFile image) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id, false);

        if ((dto.getName() != null && !dto.getName().isBlank()) && (!zonaMuscular.getName().equals(dto.getName()))) {
            zonaMuscular.setName(dto.getName());
        }

        if ((dto.getActive() != null) && (!zonaMuscular.getActive().equals(dto.getActive()))) {
            zonaMuscular.setActive(dto.getActive());
        }

        if (image != null) {
            if (!image.isEmpty()) {
                cloudinaryService.delete(zonaMuscular.getImagePublicId());
                var res = cloudinaryService.upload(image, "muscle-groups");
                if (res != null) {
                    zonaMuscular.setImageUrl(res.url());
                    zonaMuscular.setImagePublicId(res.publicId());
                }
            }
        }

        return zonaMuscularMapper.toResponseDTO(zonaMuscular);
    }

    @Transactional
    public ZonaMuscularResponseDTO removeImage(Long id) {
        ZonaMuscular zonaMuscular = getZonaMuscularOrThrow(id, false);
        cloudinaryService.delete(zonaMuscular.getImagePublicId());
        zonaMuscular.setImagePublicId(null);
        zonaMuscular.setImageUrl(null);
        return zonaMuscularMapper.toResponseDTO(zonaMuscularRepository.save(zonaMuscular));
    }

    // Métodos auxiliares
    public ZonaMuscular getZonaMuscularOrThrow(Long id, boolean verifyActive) {
        ZonaMuscular zonaMuscular = zonaMuscularRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zona Muscular con el ID " + id + " no encontrada"));

        if (verifyActive && !zonaMuscular.getActive()) {
            throw new NotFoundException("Zona Muscular con el ID " + id + " inactiva");
        }

        return zonaMuscular;
    }

    @Transactional
    void deactivateRelatedEjercicios(Musculo musculo) {
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