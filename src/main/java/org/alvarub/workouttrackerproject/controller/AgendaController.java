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
    public ResponseEntity<Object> getAgendaById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean includeUser) {
        Object response = includeUser
                ? agendaService.findById(id)
                : agendaService.findByIdSimple(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AgendaRutinaDTO>> getAllAgendasByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(agendaService.findAllByUserId(userId));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AgendaResponseDTO> markAsCompleted(@PathVariable Long id,
                                                             @Valid @RequestBody(required = false) AgendaCompleteRequestDTO dto) {
        AgendaResponseDTO updatedAgenda = agendaService.markAsCompleted(id, dto);
        return ResponseEntity.ok(updatedAgenda);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> updateAgenda(@PathVariable Long id,
                                                          @Valid @RequestBody AgendaUpdateRequestDTO dto) {
        AgendaResponseDTO updated = agendaService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

}
