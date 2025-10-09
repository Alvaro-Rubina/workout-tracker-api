package org.alvarub.workouttrackerproject.controller;

import com.auth0.exception.Auth0Exception;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.SignupRequestDTO;
import org.alvarub.workouttrackerproject.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.alvarub.workouttrackerproject.utils.Constants.ADMIN_ROL_NAME;
import static org.alvarub.workouttrackerproject.utils.Constants.USER_ROL_NAME;

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
        String auth0UserId = jwt.getSubject();
        String auth0UserEmail = jwt.getClaim(audience + "/email");
        String auth0UserName = jwt.getClaim(audience + "/name");
        return ResponseEntity.ok(usuarioService.getCurrentUsuario(auth0UserId, auth0UserEmail, auth0UserName));
    }

    @PostMapping("/signup")
    public ResponseEntity<UsuarioResponseDTO> registerUsuario(@AuthenticationPrincipal Jwt jwt) throws Auth0Exception {
        String auth0UserId = jwt.getSubject();
        String auth0UserEmail = jwt.getClaim(audience + "/email");
        String auth0UserName = jwt.getClaim(audience + "/name");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registerUser(auth0UserId, auth0UserEmail, auth0UserName));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<UsuarioStatsDTO> getUsuarioStatsById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findStatsById(id));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAllByRolName(USER_ROL_NAME));
    }

    @GetMapping("/admin/admins")
    public ResponseEntity<List<UsuarioResponseDTO>> getAllAdmins() {
        return ResponseEntity.ok(usuarioService.findAllByRolName(ADMIN_ROL_NAME));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<List<UsuarioStatsDTO>> getAllUsuariosStats() {
        return ResponseEntity.ok(usuarioService.findAllStats());
    }

    @PatchMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@AuthenticationPrincipal Jwt jwt,
                                                            @Valid @RequestPart("data") UsuarioUpdateRequestDTO dto,
                                                            @RequestPart(value = "image", required = false) MultipartFile image) throws Auth0Exception {
        String auth0userId = jwt.getSubject();
        return ResponseEntity.ok(usuarioService.update(auth0userId, dto, image));
    }

    @PreAuthorize("hasRole('PROPIETARIO')")
    @PostMapping("/admin/signup")
    public ResponseEntity<UsuarioResponseDTO> registerAdmin(@RequestBody SignupRequestDTO request) throws Auth0Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registerAdmin(request));
    }

    @PatchMapping("/admin/{id}/toggle-active")
    public ResponseEntity<UsuarioResponseDTO> toggleUsuarioActiveStatus(@PathVariable Long id,
                                                                        @AuthenticationPrincipal Jwt jwt) throws Auth0Exception {
        return ResponseEntity.ok(usuarioService.toggleActive(id));
    }

}
