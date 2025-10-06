package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.*;
import org.alvarub.workouttrackerproject.service.AgendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> createAgenda(@AuthenticationPrincipal Jwt jwt,
                                                          @Valid @RequestBody AgendaRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(agendaService.save(dto, auth0UserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAgendaById(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean includeUser) {
        String auth0UserId = jwt.getSubject();
        Object response = includeUser
                ? agendaService.findById(id, auth0UserId)
                : agendaService.findByIdSimple(id, auth0UserId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<AgendaRutinaDTO>> getAllAgendasByUser(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(agendaService.findAllByUserId(auth0UserId));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AgendaResponseDTO> markAsCompleted(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable Long id,
                                                             @Valid @RequestBody(required = false) AgendaCompleteRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        AgendaResponseDTO updatedAgenda = agendaService.markAsCompleted(id, auth0UserId, dto);
        return ResponseEntity.ok(updatedAgenda);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> updateAgenda(@AuthenticationPrincipal Jwt jwt,
                                                          @PathVariable Long id,
                                                          @Valid @RequestBody AgendaUpdateRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        AgendaResponseDTO updated = agendaService.update(id, auth0UserId, dto);
        return ResponseEntity.ok(updated);
    }

    // TODO: METODO PARA ELIMINAR UNA AGENDA
    public ResponseEntity<Void> deleteAgenda(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        agendaService.delete(id, auth0UserId);
        return ResponseEntity.noContent().build();
    }

}
