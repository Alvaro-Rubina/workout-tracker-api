package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.musculo.MusculoSimpleDTO;
import org.alvarub.workouttrackerproject.service.MusculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/muscles")
@RequiredArgsConstructor
public class MusculoController {

    private final MusculoService musculoService;

    @PostMapping("/admin")
    public ResponseEntity<MusculoResponseDTO> createMusculo(@Valid @RequestBody MusculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(musculoService.save(dto));
    }


    @GetMapping("/admin/{id}")
    public ResponseEntity<Object> getMusculoById(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? musculoService.findById(id, false)
                : musculoService.findByIdSimple(id, false);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMusculoByIdVerifyActive(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? musculoService.findById(id, true)
                : musculoService.findByIdSimple(id, true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<?>> getAllMusculos(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? musculoService.findAll()
                : musculoService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllActiveMusculos(@RequestParam(defaultValue = "false") Boolean relations) {

        List<?> response = relations
                ? musculoService.findAllActive()
                : musculoService.findAllSimpleActive();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/{id}/toggle-active")
    public ResponseEntity<MusculoSimpleDTO> toggleMusculoActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(musculoService.toggleActive(id));
    }

    @PatchMapping("/admin/{id}/deactivate")
    public ResponseEntity<MusculoSimpleDTO> deactivateMusculo(@PathVariable Long id) {
        return ResponseEntity.ok(musculoService.softDelete(id));
    }
}
