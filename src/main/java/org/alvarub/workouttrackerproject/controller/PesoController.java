package org.alvarub.workouttrackerproject.controller;

import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.peso.PesoResponseDTO;
import org.alvarub.workouttrackerproject.service.PesoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/body-weights")
@RequiredArgsConstructor
public class PesoController {

    private final PesoService pesoService;

    @PostMapping
    public ResponseEntity<PesoResponseDTO> createBodyWeight(@AuthenticationPrincipal Jwt jwt,
                                                            @Valid PesoRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pesoService.save(auth0UserId, dto));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<PesoResponseDTO> updateBodyWeight(@AuthenticationPrincipal Jwt jwt,
                                                            @PathVariable Long id,
                                                            @Valid PesoRequestDTO dto, ServletResponse servletResponse) {
        String auth0UserID = jwt.getSubject();
        return ResponseEntity.ok(pesoService.update(auth0UserID, id, dto));
    }
}
