package org.alvarub.workouttrackerproject.controller;

import com.auth0.exception.Auth0Exception;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolResponseDTO;
import org.alvarub.workouttrackerproject.service.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponseDTO> createRol(@Valid @RequestBody RolRequestDTO dto) throws Auth0Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rolService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> getRolById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(rolService.findAll());
    }
}
