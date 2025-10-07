package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.equipamiento.EquipamientoUpdateRequestDTO;
import org.alvarub.workouttrackerproject.service.EquipamientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipamientoController {

    private final EquipamientoService equipamientoService;

    @PostMapping(value = "/admin", consumes = {"multipart/form-data"})
    public ResponseEntity<EquipamientoResponseDTO> createEquipamiento(@Valid @RequestBody EquipamientoRequestDTO dto,
                                                                      @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equipamientoService.save(dto, image));
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

    @PatchMapping(value = "/admin/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<EquipamientoResponseDTO> updateEquipamiento(@PathVariable Long id,
                                                                      @Valid @RequestBody EquipamientoUpdateRequestDTO dto,
                                                                      @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(equipamientoService.update(id, dto, image));
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
