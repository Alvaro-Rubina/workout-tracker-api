package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioSimpleDTO;
import org.alvarub.workouttrackerproject.service.EjercicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService ejercicioService;

    @PostMapping
    public ResponseEntity<EjercicioResponseDTO> createEjercicio(@Valid @RequestBody EjercicioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ejercicioService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEjercicioById(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? ejercicioService.findById(id)
                : ejercicioService.findByIdSimple(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllEjercicios(@RequestParam(defaultValue = "false") Boolean relations) {

        List<?> response = relations
                ? ejercicioService.findAll()
                : ejercicioService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<EjercicioSimpleDTO> toggleEjercicioActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEjercicioSoft(@PathVariable Long id) {
        ejercicioService.softDelete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
