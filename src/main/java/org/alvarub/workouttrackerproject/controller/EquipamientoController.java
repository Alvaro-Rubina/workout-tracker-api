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

    @PostMapping
    public ResponseEntity<EquipamientoResponseDTO> createEquipamiento(@Valid @RequestBody EquipamientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equipamientoService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamientoResponseDTO> getEquipamientoById(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<EquipamientoResponseDTO>> getAllEquipamientos() {
        return ResponseEntity.ok(equipamientoService.findAll());
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<EquipamientoResponseDTO> toggleEquipamientoActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.toggleActive(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<EquipamientoResponseDTO> deactivateEquipamiento(@PathVariable Long id) {
        return ResponseEntity.ok(equipamientoService.softDelete(id));
    }
}
