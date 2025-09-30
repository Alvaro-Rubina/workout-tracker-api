package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.ejercicio.EjercicioUpdateRequestDTO;
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

    @PostMapping("/admin")
    public ResponseEntity<EjercicioResponseDTO> createEjercicio(@Valid @RequestBody EjercicioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ejercicioService.save(dto));
    }

    @GetMapping("/admin/{id}") // NOTE: Para admins (pueden obtener todos los ejercicios)
    public ResponseEntity<Object> getEjercicioById(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? ejercicioService.findById(id, false)
                : ejercicioService.findByIdSimple(id, false);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}") // NOTE: Para usuarios (pueden obtener solo los que estan activos)
    public ResponseEntity<Object> getEjercicioByIdVerifyActive(@PathVariable Long id,
                                                               @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? ejercicioService.findById(id, true)
                : ejercicioService.findByIdSimple(id, true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin") // NOTE: Para admins (pueden obtener todos los ejercicios)
    public ResponseEntity<List<?>> getAllEjercicios(@RequestParam(defaultValue = "false") Boolean relations) {

        List<?> response = relations
                ? ejercicioService.findAll()
                : ejercicioService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @GetMapping // NOTE: Para usuarios (pueden obtener solo los que estan activos)
    public ResponseEntity<List<?>> getAllActiveEjercicios(@RequestParam(defaultValue = "false") Boolean relations) {

        List<?> response = relations
                ? ejercicioService.findAllActive()
                : ejercicioService.findAllSimpleActive();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/{id}/toggle-active")
    public ResponseEntity<EjercicioSimpleDTO> toggleEjercicioActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.toggleActive(id));
    }

    @PatchMapping("/admin/{id}/deactivate")
    public ResponseEntity<EjercicioSimpleDTO> deactivateEjercicio(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.softDelete(id));
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<EjercicioResponseDTO> updateEjercicio(@PathVariable Long id,
                                                                @Valid @RequestBody EjercicioUpdateRequestDTO dto) {
        return ResponseEntity.ok(ejercicioService.update(id, dto));
    }

}
