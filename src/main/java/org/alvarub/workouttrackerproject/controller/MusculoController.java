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
@RequestMapping("/musculos")
@RequiredArgsConstructor
public class MusculoController {

    private final MusculoService musculoService;

    @PostMapping
    public ResponseEntity<MusculoResponseDTO> createMusculo(@Valid @RequestBody MusculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(musculoService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMusculoById(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? musculoService.findById(id)
                : musculoService.findByIdSimple(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllMusculos(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? musculoService.findAll()
                : musculoService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<MusculoSimpleDTO> toggleMusculoActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(musculoService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusculoSoft(@PathVariable Long id) {
        musculoService.softDelete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
