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

    @PostMapping("/admin")
    public ResponseEntity<CategoriaResponseDTO> createCategoria(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.save(dto));
    }

    @GetMapping("/admin/{id}") // NOTE: Para admins (pueden obtener todas las categorias)
    public ResponseEntity<CategoriaResponseDTO> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id, false));
    }

    @GetMapping("/{id}") // NOTE: Para clientes (pueden obtener solo las que estan activas)
    public ResponseEntity<CategoriaResponseDTO> getCategoriaByIdVerifyActive(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id, true));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<CategoriaResponseDTO>> getAllCategorias() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> getAllActiveCategorias() {
        return ResponseEntity.ok(categoriaService.findAllActive());
    }

    @PatchMapping("/admin/{id}/toggle-active")
    public ResponseEntity<CategoriaResponseDTO> toggleCategoriaActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.toggleActive(id));
    }

    @PatchMapping("/admin/{id}/deactivate")
    public ResponseEntity<CategoriaResponseDTO> deactivateCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.softDelete(id));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        categoriaService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
