package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaCompleteRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.agenda.AgendaUpdateRequestDTO;
import org.alvarub.workouttrackerproject.service.AgendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> createAgenda(@Valid @RequestBody AgendaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(agendaService.save(dto));
    }

    @GetMapping
    public ResponseEntity<Object> getAllAgendas(@RequestParam(defaultValue = "false") Boolean includeUser) {
        Object response = includeUser
                ? agendaService.findAll()
                : agendaService.findAllSimple();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAgendaById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean includeUser) {
        Object response = includeUser
                ? agendaService.findById(id)
                : agendaService.findByIdSimple(id);

        return ResponseEntity.ok(response);
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
