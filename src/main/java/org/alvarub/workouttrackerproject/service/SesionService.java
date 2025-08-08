package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.SesionMapper;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Sesion;
import org.alvarub.workouttrackerproject.persistence.repository.SesionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SesionService {

    // TODO: Solo faltaría el metodo update, y a lo mejor no es necesario si es que actualizo cada sesión
    //  desde la rutina. Ver eso

    private final SesionRepository sesionRepository;
    private final SesionMapper sesionMapper;
    private final CategoriaService categoriaService;
    private final EjercicioService ejercicioService;

    @Transactional
    public SesionResponseDTO save(SesionRequestDTO dto) {
        Sesion sesion = sesionMapper.toEntity(dto);

        sesion.setCategory(categoriaService.getCategoriaOrThrow(dto.getCategoryId(), true));

        // A cada SesionEjercicio de la sesion le seteo la sesión y el ejercicio (validando este último)
        sesion.getSessionExercises().forEach(sesionEjercicio -> {
            sesionEjercicio.setSession(sesion);

            Long ejercicioId = sesionEjercicio.getExercise().getId();
            sesionEjercicio.setExercise(ejercicioService.getEjercicioOrThrow(ejercicioId, true));
        });

        return sesionMapper.toResponseDTO(sesionRepository.save(sesion));
    }

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

    @Transactional
    public void hardDelete(Long id) {
        Sesion sesion = getSesionOrThrow(id);
        sesionRepository.delete(sesion);
    }

    // Métodos auxiliares
    public Sesion getSesionOrThrow(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sesión con el ID " + id + " no encontrada"));
    }

}
