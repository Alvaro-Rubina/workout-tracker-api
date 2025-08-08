package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.RutinaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
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
    private final UsuarioRepository usuarioRepository;
    private final AgendaRepository agendaRepository;

    @Transactional
    public RutinaResponseDTO save(RutinaRequestDTO dto) {
        Rutina rutina = rutinaMapper.toEntity(dto);

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
}
