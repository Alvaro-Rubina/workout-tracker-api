package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.ExistingResourceException;
import org.alvarub.workouttrackerproject.exception.ForbiddenOperationException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.AgendaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRutinaDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Agenda;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.alvarub.workouttrackerproject.persistence.entity.Sesion;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
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

        Rutina rutina = rutinaService.getRutinaOrThrow(dto.getRoutineId());
        if (!Boolean.TRUE.equals(rutina.getIsPublic())) {
            if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para agendar una rutina privada que no le pertenece");
            }
        }
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        if (agendaRepository.existsByUser_IdAndRoutine_Id(usuario.getId(), rutina.getId())) {
            throw new ExistingResourceException("El usuario proporcionado ya tiene agendada la rutina indicada");
        }

        // Calcular el startDate basándose en los días de las sesiones de la rutina
        LocalDateTime calculatedStartDate = calculateStartDate(rutina);
        agenda.setStartDate(calculatedStartDate);

        agenda.setUser(usuario);
        agenda.setRoutine(rutina);
        return agendaMapper.toResponseDTO(agendaRepository.save(agenda));
    }

    @Transactional(readOnly = true)
    public AgendaResponseDTO findById(Long id, String auth0UserId) {
        Agenda agenda = getAgendaOrThrow(id);

        if (!agenda.getUser().getAuth0Id().equals(auth0UserId)) {
            throw new ForbiddenOperationException("Usuario sin permiso para obtener la agenda de otro usuario");
        }

        return agendaMapper.toResponseDTO(agenda);
    }

    @Transactional(readOnly = true)
    public AgendaRutinaDTO findByIdSimple(Long id, String auth0UserId) {
        Agenda agenda = getAgendaOrThrow(id);

        if (!agenda.getUser().getAuth0Id().equals(auth0UserId)) {
            throw new ForbiddenOperationException("Usuario sin permiso para obtener la agenda de otro usuario");
        }

        return agendaMapper.toRutinaDTO(agenda);
    }

    @Transactional(readOnly = true)
    public List<AgendaRutinaDTO> findAllByUserId(String auth0UserId) {
        return agendaRepository.findByUser_Auth0Id(auth0UserId)
                .stream()
                .map(agendaMapper::toRutinaDTO)
                .toList();
    }

    @Transactional
    public AgendaResponseDTO update(Long id, String auth0UserId, AgendaUpdateRequestDTO dto) {
        Agenda agenda = getAgendaOrThrow(id);

        if (!agenda.getUser().getAuth0Id().equals(auth0UserId)) {
            throw new ForbiddenOperationException("Usuario sin permiso para modificar la agenda de otro usuario");
        }

        if (dto.getStartDate() != null) {
            agenda.setStartDate(dto.getStartDate());
        }

        if (dto.getReminderMinutes() != null) {
            agenda.setReminderMinutes(dto.getReminderMinutes());
        }

        if (dto.getComment() != null) {
            agenda.setComment(dto.getComment());
        }

        return agendaMapper.toResponseDTO(agendaRepository.save(agenda));
    }

    public void delete(Long id, String auth0UserId) {
        Agenda agenda = getAgendaOrThrow(id);

        if (!agenda.getUser().getAuth0Id().equals(auth0UserId)) {
            throw new ForbiddenOperationException("Usuario sin permiso para eliminar la agenda de otro usuario");
        }

        agendaRepository.delete(agenda);
    }

    // Métodos auxiliares
    public Agenda getAgendaOrThrow(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agenda con el id " + id + " no encontrada"));
    }

    /**
     * Calcula la fecha de inicio de la agenda basándose en los días de las sesiones de la rutina.
     * Toma el domingo como primer día de la semana.
     *
     * @param rutina La rutina a agendar
     * @return La fecha de inicio calculada
     */
    private LocalDateTime calculateStartDate(Rutina rutina) {
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();

        // Obtener el primer día de sesión (el más cercano al inicio de la semana)
        DayOfWeek firstSessionDay = rutina.getSessions().stream()
                .map(Sesion::getDayOfWeek)
                .min(Comparator.comparingInt(this::dayOfWeekToSundayBasedOrder))
                .orElseThrow(() -> new IllegalStateException("La rutina no tiene sesiones definidas"));

        // Convertir el día actual a orden basado en domingo (Domingo=0, Lunes=1, ..., Sábado=6)
        int currentDayOrder = dayOfWeekToSundayBasedOrder(currentDayOfWeek);
        int firstSessionDayOrder = dayOfWeekToSundayBasedOrder(firstSessionDay);

        LocalDate startDate;

        // Si el primer día de sesión ya pasó en la semana actual, comenzar la próxima semana
        if (firstSessionDayOrder < currentDayOrder) {
            // Ir al próximo domingo y luego al día de la sesión
            startDate = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                    .with(TemporalAdjusters.nextOrSame(firstSessionDay));
        } else {
            // El día de sesión es hoy o está más adelante en la semana actual
            startDate = today.with(TemporalAdjusters.nextOrSame(firstSessionDay));
        }

        // Retornar la fecha con hora al inicio del día
        return startDate.atStartOfDay();
    }

    /**
     * Convierte DayOfWeek a un orden basado en domingo como primer día de la semana.
     * Domingo = 0, Lunes = 1, Martes = 2, ..., Sábado = 6
     *
     * @param dayOfWeek El día de la semana
     * @return El orden del día (0-6)
     */
    private int dayOfWeekToSundayBasedOrder(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
    }
}
