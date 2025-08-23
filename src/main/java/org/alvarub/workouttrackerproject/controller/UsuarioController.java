package org.alvarub.workouttrackerproject.controller;

import com.auth0.exception.Auth0Exception;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getCurrentUsuario(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.getUsuarioFromToken(jwt));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@AuthenticationPrincipal Jwt jwt,
                                                            @Valid @RequestBody UsuarioRequestDTO dto) throws Auth0Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.saveUser(dto, jwt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<UsuarioStatsDTO> getUsuarioStatsById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findStatsById(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<List<UsuarioStatsDTO>> getAllUsuariosStats() {
        return ResponseEntity.ok(usuarioService.findAllStats());
    }


    // ENDPOINTS ADMIN
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede crear otro ADMIN
    public ResponseEntity<UsuarioResponseDTO> createAdmin(@AuthenticationPrincipal Jwt jwt,
                                                          @Valid @RequestBody UsuarioRequestDTO dto) throws Auth0Exception {
        UsuarioResponseDTO saved = usuarioService.saveAdmin(dto, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> toggleUsuarioActiveStatus(@PathVariable Long id,
                                                                        @AuthenticationPrincipal Jwt jwt) throws Auth0Exception {
        return ResponseEntity.ok(usuarioService.toggleActive(id, jwt));
    }

}
