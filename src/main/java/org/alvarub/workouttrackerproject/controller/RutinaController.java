package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rutina.RutinaUpdateRequestDTO;
import org.alvarub.workouttrackerproject.service.RutinaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routines")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    @PostMapping
    public ResponseEntity<RutinaResponseDTO> createRutina(@AuthenticationPrincipal Jwt jwt,
                                                          @Valid @RequestBody RutinaRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rutinaService.save(dto, auth0UserId));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<Object> getRutinaById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? rutinaService.findById(id)
                : rutinaService.findByIdSimple(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRutinaByIdVisibleToUser(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable Long id,
                                                             @RequestParam(defaultValue = "false") Boolean relations) {
        String auth0UserId = jwt.getSubject();
        Object response = relations
                ? rutinaService.findByIdVisibleToUser(id, auth0UserId)
                : rutinaService.findByIdSimpleVisibleToUser(id, auth0UserId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<?>> getAllRutinas(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? rutinaService.findAll()
                :  rutinaService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<?>> getAllUserRutinas(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestParam(defaultValue = "false") Boolean relations) {
        String auth0UserId = jwt.getSubject();
        List<?> response = relations
                ? rutinaService.findAllByUser(auth0UserId)
                :  rutinaService.findAllSimpleByUser(auth0UserId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<List<?>> getAllPublicRutinas(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? rutinaService.findAllPublic()
                :  rutinaService.findAllPublicSimple();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<RutinaSimpleDTO> toggleRutinaIsPublicStatus(@AuthenticationPrincipal Jwt jwt,
                                                                      @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.toggleIsPublic(id, auth0UserId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RutinaResponseDTO> updateRutina(@AuthenticationPrincipal Jwt jwt,
                                                          @PathVariable Long id,
                                                          @RequestBody RutinaUpdateRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.update(id, auth0UserId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRutina(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        rutinaService.hardDelete(id, auth0UserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<RutinaSimpleDTO> toggleLikeOnRutina(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.toggleLikeOnRoutine(id, auth0UserId));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<RutinaSimpleDTO> toggleSaveOnRutina(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.toggleSaveOnRoutine(id, auth0UserId));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RutinaSimpleDTO> toggleCompletedOnRoutine(@AuthenticationPrincipal Jwt jwt,
                                                                    @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.toggleCompleteOnRoutine(id, auth0UserId));
    }

    @GetMapping("/liked")
    public ResponseEntity<List<RutinaResponseDTO>> getUserLikedRutinas(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.findAllLiked(auth0UserId));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<RutinaResponseDTO>> getUserSavedRutinas(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.findAllSaved(auth0UserId));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<RutinaResponseDTO>> getUserCompletedRutinas(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(rutinaService.findAllCompleted(auth0UserId));
    }
}
