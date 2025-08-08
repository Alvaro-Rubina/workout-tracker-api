package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaResponseDTO;
import org.alvarub.workouttrackerproject.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> createCategoria(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> getAllCategorias() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<CategoriaResponseDTO> toggleCategoriaActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.toggleActive(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CategoriaResponseDTO> deactivateCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.softDelete(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        categoriaService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
