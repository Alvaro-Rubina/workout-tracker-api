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

import java.util.List;

@RestController
@RequestMapping("/body-weights")
@RequiredArgsConstructor
public class PesoController {

    private final PesoService pesoService;

    @PostMapping
    public ResponseEntity<PesoResponseDTO> createBodyWeight(@AuthenticationPrincipal Jwt jwt,
                                                            @Valid @RequestBody PesoRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pesoService.save(auth0UserId, dto));

    }

    @GetMapping("/last")
    public ResponseEntity<PesoResponseDTO> getUserLastBodyWeight(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(pesoService.getUserLastBodyWeight(auth0UserId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PesoResponseDTO>> getUserBodyWeights(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(pesoService.getUserBodyWeights(auth0UserId));
    }

    @PatchMapping()
    public ResponseEntity<PesoResponseDTO> updateLastBodyWeight(@AuthenticationPrincipal Jwt jwt,
                                                                @PathVariable Long id,
                                                                @Valid PesoRequestDTO dto, ServletResponse servletResponse) {
        String auth0UserID = jwt.getSubject();
        return ResponseEntity.ok(pesoService.updateLast(auth0UserID, dto));
    }
}
