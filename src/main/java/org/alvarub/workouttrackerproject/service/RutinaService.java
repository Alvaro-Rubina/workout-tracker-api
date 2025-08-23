package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.RutinaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesionejercicio.SesionEjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.*;
import org.alvarub.workouttrackerproject.persistence.repository.AgendaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.RutinaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashSet;

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

    @Transactional
    public RutinaResponseDTO save(RutinaRequestDTO dto) {
        Rutina rutina = rutinaMapper.toEntity(dto);

        rutina.setUser(usuarioService.getUsuarioOrThrow(dto.getUserId(), true));

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

    @Transactional
    public RutinaSimpleDTO toggleIsPublic(Long id) {
        Rutina rutina = getRutinaOrThrow(id);
        rutina.setIsPublic(!rutina.getIsPublic());
        return rutinaMapper.toSimpleDTO(rutinaRepository.save(rutina));
    }

    @Transactional
    public void hardDelete(Long id) {
        Rutina rutina = getRutinaOrThrow(id);

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

    // Métodos auxiliares
    public Rutina getRutinaOrThrow(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rutina con el ID " + id + " no encontrada"));
    }

    @Transactional
    public RutinaResponseDTO update(Long id, RutinaRequestDTO dto) {
        Rutina rutina = getRutinaOrThrow(id);

        // Campos básicos
        rutina.setName(dto.getName());
        rutina.setDescription(dto.getDescription());
        rutina.setIsPublic(dto.getIsPublic());
        rutina.setDifficulty(dto.getDifficulty());

        // Relaciones directas
        rutina.setCategory(categoriaService.getCategoriaOrThrow(dto.getCategoryId(), true));
        rutina.setUser(usuarioService.getUsuarioOrThrow(dto.getUserId(), true));

        // Reemplazo completo de sesiones y ejercicios de sesión
        rutina.getSessions().clear();

        dto.getSessions().forEach(sDto -> {
            Sesion sesion = new Sesion();
            sesion.setName(sDto.getName());
            sesion.setDescription(sDto.getDescription());
            sesion.setDayOfWeek(sDto.getDayOfWeek());
            sesion.setCategory(categoriaService.getCategoriaOrThrow(sDto.getCategoryId(), true));
            sesion.setRoutine(rutina);

            sesion.setSessionExercises(new LinkedHashSet<>());

            sDto.getSessionExercises().forEach(seDto -> {
                SesionEjercicio se = new SesionEjercicio();
                se.setSets(seDto.getSets());
                se.setReps(seDto.getReps());
                se.setRestBetweenSets(seDto.getRestBetweenSets());
                se.setComment(seDto.getComment());
                se.setSession(sesion);
                se.setExercise(ejercicioService.getEjercicioOrThrow(seDto.getExerciseId(), true));

                sesion.getSessionExercises().add(se);
            });

            rutina.getSessions().add(sesion);
        });

        return rutinaMapper.toResponseDTO(rutinaRepository.save(rutina));
    }
}
