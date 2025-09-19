package org.alvarub.workouttrackerproject.controller;

import com.auth0.exception.Auth0Exception;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auth0.audience}")
    private String audience;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getCurrentUsuario(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.getUsuarioFromToken(jwt));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@AuthenticationPrincipal Jwt jwt) throws Auth0Exception {
        String auth0UserId = jwt.getSubject();
        String auth0UserEmail = jwt.getClaim(audience + "/email");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.saveUser(auth0UserId, auth0UserEmail));
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
    public ResponseEntity<UsuarioResponseDTO> createAdmin(@AuthenticationPrincipal Jwt jwt) throws Auth0Exception {
        String auth0UserId = jwt.getSubject();
        String auth0UserEmail = jwt.getClaim(audience + "/email");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.saveAdmin(auth0UserId, auth0UserEmail));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> toggleUsuarioActiveStatus(@PathVariable Long id,
                                                                        @AuthenticationPrincipal Jwt jwt) throws Auth0Exception {
        return ResponseEntity.ok(usuarioService.toggleActive(id));
    }

}
