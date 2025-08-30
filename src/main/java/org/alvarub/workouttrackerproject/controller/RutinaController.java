package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaUpdateRequestDTO;
import org.alvarub.workouttrackerproject.service.RutinaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routines")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    @PostMapping
    public ResponseEntity<RutinaResponseDTO> createRutina(@Valid @RequestBody RutinaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rutinaService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRutinaById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? rutinaService.findById(id)
                : rutinaService.findByIdSimple(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllRutinas(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? rutinaService.findAll()
                :  rutinaService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<RutinaSimpleDTO> toggleRutinaIsPublicStatus(@PathVariable Long id) {
        return ResponseEntity.ok(rutinaService.toggleIsPublic(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RutinaResponseDTO> updateRutina(@PathVariable Long id,
                                                          @RequestBody RutinaUpdateRequestDTO dto) {
        return ResponseEntity.ok(rutinaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Long id) {
        rutinaService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
