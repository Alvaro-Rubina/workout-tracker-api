package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.BusinessException;
import org.alvarub.workouttrackerproject.exception.ForbiddenOperationException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.RutinaMapper;
import org.alvarub.workouttrackerproject.mapper.SesionMapper;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio.SesionEjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.*;
import org.alvarub.workouttrackerproject.persistence.repository.AgendaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.RutinaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final RutinaMapper rutinaMapper;
    private final EjercicioService ejercicioService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final AgendaRepository agendaRepository;
    private final SesionMapper sesionMapper;

    @Transactional
    public RutinaResponseDTO save(RutinaRequestDTO dto, String auth0UserId) {
        Rutina rutina = rutinaMapper.toEntity(dto);

        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);
        rutina.setUser(usuario);

        rutina.setCategory(categoriaService.getCategoriaOrThrow(dto.getCategoryId(), true));

        rutina.getSessions().forEach(sesion -> {
            sesion.setRoutine(rutina);
            sesion.setCategory(categoriaService.getCategoriaOrThrow(sesion.getCategory().getId(), true));

            sesion.getSessionExercises().forEach(sesionEjercicio -> {
                sesionEjercicio.setSession(sesion);

                Long ejercicioId = sesionEjercicio.getExercise().getId();
                sesionEjercicio.setExercise(ejercicioService.getEjercicioOrThrow(ejercicioId, true));
            });
        });

        return rutinaMapper.toResponseDTO(rutinaRepository.save(rutina));
    }

    @Transactional(readOnly = true)
    public RutinaResponseDTO findById(Long id) {
        Rutina rutina = getRutinaOrThrow(id);
        return rutinaMapper.toResponseDTO(rutina);
    }

    @Transactional(readOnly = true)
    public RutinaSimpleDTO findByIdSimple(Long id) {
        Rutina rutina = getRutinaOrThrow(id);
        return rutinaMapper.toSimpleDTO(rutina);
    }

    @Transactional(readOnly = true)
    public RutinaResponseDTO findByIdVisibleToUser(Long id, String auth0UserId) {
        Rutina rutina = getRutinaOrThrow(id);

        if (!Boolean.TRUE.equals(rutina.getIsPublic())) {
            // Solo el creador puede verla si es privada
            if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para obtener una rutina privada que no le pertenece");
            }
        }

        return rutinaMapper.toResponseDTO(rutina);
    }

    @Transactional(readOnly = true)
    public RutinaSimpleDTO findByIdSimpleVisibleToUser(Long id, String auth0UserId) {
        Rutina rutina = getRutinaOrThrow(id);

        if (!Boolean.TRUE.equals(rutina.getIsPublic())) {
            if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para obtener una rutina privada que no le pertenece");
            }
        }

        return rutinaMapper.toSimpleDTO(rutina);
    }

    @Transactional(readOnly = true)
    public List<RutinaResponseDTO> findAll() {
        return rutinaRepository.findAll().stream()
                .map(rutinaMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaSimpleDTO> findAllSimple() {
        return rutinaRepository.findAll().stream()
                .map(rutinaMapper::toSimpleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaResponseDTO> findAllPublic() {
        return rutinaRepository.findAll().stream()
                .filter(Rutina::getIsPublic)
                .map(rutinaMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaSimpleDTO> findAllPublicSimple() {
        return rutinaRepository.findAll().stream()
                .filter(Rutina::getIsPublic)
                .map(rutinaMapper::toSimpleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaResponseDTO> findAllByUser(String auth0UserId) {
        return rutinaRepository.findByUser_Auth0Id(auth0UserId).stream()
                .map(rutinaMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaSimpleDTO> findAllSimpleByUser(String auth0UserId) {
        return rutinaRepository.findByUser_Auth0Id(auth0UserId).stream()
                .map(rutinaMapper::toSimpleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaResponseDTO> findAllLiked(String auth0UserId) {
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        return usuario.getLikedRoutines().stream()
                .map(rutinaMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaResponseDTO> findAllSaved(String auth0UserId) {
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        return usuario.getSavedRoutines().stream()
                .map(rutinaMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public RutinaSimpleDTO toggleIsPublic(Long id, String auth0UserId) {
        Rutina rutina = getRutinaOrThrow(id);

        if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
            throw new ForbiddenOperationException("Usuario sin permiso para modificar una rutina que no le pertenece");
        }

        rutina.setIsPublic(!rutina.getIsPublic());
        return rutinaMapper.toSimpleDTO(rutina);
    }

    @Transactional
    public void hardDelete(Long id, String auth0UserId) {
        Rutina rutina = getRutinaOrThrow(id);

        if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
            throw new ForbiddenOperationException("Usuario sin permiso para modificar una rutina que no le pertenece");
        }

        // Remuevo la rutina de todas las relaciones con Usuario
        Usuario creador = usuarioRepository.findByCreatedRoutinesContains(rutina)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado para la rutina con el ID " + id));

        creador.getCreatedRoutines().remove(rutina);

        usuarioRepository.findAllByLikedRoutinesContains(rutina).forEach(usuario -> {
            usuario.getLikedRoutines().remove(rutina);
        });

        usuarioRepository.findAllBySavedRoutinesContains(rutina).forEach(usuario -> {
            usuario.getSavedRoutines().remove(rutina);
        });

        // TODO: Acá al remover la rutina de las agendas de los usuarios podria poner un log para
        //  avisar a los usuarios con agendas proximas que la rutina fué removida
        agendaRepository.findAllByRoutine(rutina).forEach(agenda -> {
            // TODO: Ver si dejo como está, seteando la rutina como null, o elimino directamente la agenda
            agenda.setRoutine(null);
        });

        rutinaRepository.delete(rutina);
    }

    @Transactional
    public RutinaResponseDTO update(Long id, String auth0UserId, RutinaUpdateRequestDTO dto) {
        Rutina rutina = getRutinaOrThrow(id);

        if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
            throw new ForbiddenOperationException("Usuario sin permiso para modificar una rutina que no le pertenece");
        }

        if (dto.getName() != null){
            rutina.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            rutina.setDescription(dto.getDescription());
        }

        if (dto.getIsPublic() != null) {
            rutina.setIsPublic(dto.getIsPublic());
        }

        if (dto.getDifficulty() != null) {
            rutina.setDifficulty(dto.getDifficulty());
        }

        if ((dto.getCategoryId() != null) && (!dto.getCategoryId().equals(rutina.getCategory().getId()))) {
            rutina.setCategory(categoriaService.getCategoriaOrThrow(dto.getCategoryId(), true));
        }

        if (dto.getSessions() != null && !dto.getSessions().isEmpty()) {
            rutina.getSessions().clear();

            dto.getSessions().forEach(sesionDTO -> {
                Sesion sesion = sesionMapper.toEntity(sesionDTO);
                sesion.setRoutine(rutina);
                sesion.setCategory(categoriaService.getCategoriaOrThrow(sesionDTO.getCategoryId(), true));

                // A cada SesionEjercicio de la sesion le seteo la sesión y el ejercicio (validando este último)
                sesion.getSessionExercises().forEach(sesionEjercicio -> {
                    sesionEjercicio.setSession(sesion);

                    Long ejercicioId = sesionEjercicio.getExercise().getId();
                    sesionEjercicio.setExercise(ejercicioService.getEjercicioOrThrow(ejercicioId, true));
                });

                rutina.getSessions().add(sesion);
            });
        }

        return rutinaMapper.toResponseDTO(rutina);
    }

    @Transactional
    public RutinaSimpleDTO toggleLikeOnRoutine(Long rutinaId, String auth0UserId) {
        Rutina rutina = getRutinaOrThrow(rutinaId);

        if (!Boolean.TRUE.equals(rutina.getIsPublic())) {
            if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para obtener una rutina privada que no le pertenece");
            }
        }

        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);
        if (usuario.getLikedRoutines().contains(rutina)) {
            usuario.getLikedRoutines().remove(rutina);
            rutina.setLikesCount(rutina.getLikesCount() - 1);
        } else {
            usuario.getLikedRoutines().add(rutina);
            rutina.setLikesCount(rutina.getLikesCount() + 1);
        }

        return rutinaMapper.toSimpleDTO(rutina);
    }

    @Transactional
    public RutinaSimpleDTO toggleSaveOnRoutine(Long rutinaId, String auth0UserId) {
        Rutina rutina = getRutinaOrThrow(rutinaId);

        if (!Boolean.TRUE.equals(rutina.getIsPublic())) {
            if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para obtener una rutina privada que no le pertenece");
            }
        }

        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);
        if (usuario.getSavedRoutines().contains(rutina)) {
            usuario.getSavedRoutines().remove(rutina);
        } else {
            usuario.getSavedRoutines().add(rutina);
        }

        return rutinaMapper.toSimpleDTO(rutina);
    }

    // Métodos auxiliares
    public Rutina getRutinaOrThrow(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rutina con el ID " + id + " no encontrada"));
    }
}
