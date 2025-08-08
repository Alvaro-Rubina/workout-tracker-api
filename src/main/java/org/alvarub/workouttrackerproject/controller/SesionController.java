package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.sesion.SesionResponseDTO;
import org.alvarub.workouttrackerproject.service.SesionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @PostMapping
    public ResponseEntity<SesionResponseDTO> createSesion(@Valid @RequestBody SesionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sesionService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSesionById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? sesionService.findById(id)
                : sesionService.findByIdSimple(id);
        return ResponseEntity.ok(response);
    }
}
