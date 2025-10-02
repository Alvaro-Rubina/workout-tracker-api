package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.ForbiddenOperationException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.SesionMapper;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Sesion;
import org.alvarub.workouttrackerproject.persistence.repository.SesionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final SesionMapper sesionMapper;

    @Transactional(readOnly = true)
    public SesionResponseDTO findById(Long id) {
        Sesion sesion = getSesionOrThrow(id);
        return sesionMapper.toResponseDTO(sesion);
    }

    @Transactional(readOnly = true)
    public SesionSimpleDTO findByIdSimple(Long id) {
        Sesion sesion = getSesionOrThrow(id);
        return sesionMapper.toSimpleDTO(sesion);
    }

    @Transactional(readOnly = true)
    public SesionResponseDTO findByIdVisibleToUser(Long id, String auth0UserId) {
        Sesion sesion = getSesionOrThrow(id);

        if (!Boolean.TRUE.equals(sesion.getRoutine().getIsPublic())) {
            // Solo el creador puede verla si es privada
            if (!auth0UserId.equals(sesion.getRoutine().getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para obtener la sesión de una rutina privada que no le pertenece");
            }
        }

        return sesionMapper.toResponseDTO(sesion);
    }

    @Transactional(readOnly = true)
    public SesionSimpleDTO findByIdSimpleVisibleToUser(Long id, String auth0UserId) {
        Sesion sesion = getSesionOrThrow(id);

        if (!Boolean.TRUE.equals(sesion.getRoutine().getIsPublic())) {
            // Solo el creador puede verla si es privada
            if (!auth0UserId.equals(sesion.getRoutine().getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para obtener la sesión de una rutina privada que no le pertenece");
            }
        }

        return sesionMapper.toSimpleDTO(sesion);
    }

    // Métodos auxiliares
    public Sesion getSesionOrThrow(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sesión con el ID " + id + " no encontrada"));
    }

}
