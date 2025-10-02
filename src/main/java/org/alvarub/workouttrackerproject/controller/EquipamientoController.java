package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.service.EquipamientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipamientoController {

    private final EquipamientoService equipamientoService;

    @PostMapping("/admin")
    public ResponseEntity<EquipamientoResponseDTO> createEquipamiento(@Valid @RequestBody EquipamientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equipamientoService.save(dto));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<EquipamientoResponseDTO> getEquipamientoById(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.findById(id, false));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamientoResponseDTO> getEquipamientoByIdVerifyActive(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.findById(id, true));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<EquipamientoResponseDTO>> getAllEquipamientos() {
        return ResponseEntity.ok(equipamientoService.findAll());
    }

    @GetMapping
    public ResponseEntity<List<EquipamientoResponseDTO>> getAllActiveEquipamientos() {
        return ResponseEntity.ok(equipamientoService.findAllActive());
    }

    @PatchMapping("/admin/{id}/toggle-active")
    public ResponseEntity<EquipamientoResponseDTO> toggleEquipamientoActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.toggleActive(id));
    }

    @PatchMapping("/admin/{id}/deactivate")
    public ResponseEntity<EquipamientoResponseDTO> deactivateEquipamiento(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.softDelete(id));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteEquipamiento(@PathVariable Long id) {
        equipamientoService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
