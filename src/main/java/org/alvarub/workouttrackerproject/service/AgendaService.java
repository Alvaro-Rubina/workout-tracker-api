package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.ForbiddenOperationException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.AgendaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaCompleteRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRutinaDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Agenda;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final UsuarioService usuarioService;
    private final RutinaService rutinaService;

    @Transactional
    public AgendaResponseDTO save(AgendaRequestDTO dto, String auth0UserId) {
        Agenda agenda = agendaMapper.toEntity(dto);

        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);
        Rutina rutina = rutinaService.getRutinaOrThrow(dto.getRoutineId());

        if (!rutina.getIsPublic() && !usuario.getCreatedRoutines().contains(rutina)) {
            throw new ForbiddenOperationException("Usuario sin permiso para agendar una rutina privada que no le pertenece");
        }

        agenda.setUser(usuario);
        agenda.setRoutine(rutina);
        return agendaMapper.toResponseDTO(agendaRepository.save(agenda));
    }

    @Transactional(readOnly = true)
    public AgendaResponseDTO findById(Long id) {
        Agenda agenda = getAgendaOrThrow(id);
        return agendaMapper.toResponseDTO(agenda);
    }

    @Transactional(readOnly = true)
    public AgendaRutinaDTO findByIdSimple(Long id) {
        Agenda agenda = getAgendaOrThrow(id);
        return agendaMapper.toRutinaDTO(agenda);
    }

    @Transactional(readOnly = true)
    public List<AgendaRutinaDTO> findAllByUserId(Long userId) {
        return agendaRepository.findByUserId(userId)
                .stream()
                .map(agendaMapper::toRutinaDTO)
                .toList();
    }

    @Transactional
    public AgendaResponseDTO markAsCompleted(Long id, AgendaCompleteRequestDTO dto) {
        Agenda agenda = getAgendaOrThrow(id);

        // Si la agenda ya fué completada anteriormente simplemente la retorno
        if (agenda.getCompleted().equals(true)) {
            return agendaMapper.toResponseDTO(agenda);
        }

        agenda.setCompleted(true);
        agenda.setCompletedAt(LocalDateTime.now());

        if (dto != null && dto.comment() != null && !dto.comment().isBlank()) {
            agenda.setComment(dto.comment());
        }

        return agendaMapper.toResponseDTO(agendaRepository.save(agenda));
    }

    @Transactional
    public AgendaResponseDTO update(Long id, AgendaUpdateRequestDTO dto) {
        Agenda agenda = getAgendaOrThrow(id);

        if (dto.getStartDate() != null) {
            agenda.setStartDate(dto.getStartDate());
        }

        if (dto.getReminderMinutes() != null) {
            agenda.setReminderMinutes(dto.getReminderMinutes());
        }

        if (dto.getComment() != null) {
            agenda.setComment(dto.getComment());
        }

        if (dto.getUserId() != null) {
            agenda.setUser(usuarioService.getUsuarioOrThrow(dto.getUserId(), true));
        }

        if (dto.getRoutineId() != null) {
            agenda.setRoutine(rutinaService.getRutinaOrThrow(dto.getRoutineId()));
        }

        return agendaMapper.toResponseDTO(agendaRepository.save(agenda));
    }

    // Métodos auxiliares
    public Agenda getAgendaOrThrow(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agenda con el id " + id + " no encontrada"));
    }
}
