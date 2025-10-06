package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioContentRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioSimpleDTO;
import org.alvarub.workouttrackerproject.service.ComentarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> createComentario(@AuthenticationPrincipal Jwt jwt,
                                                                  @Valid @RequestBody ComentarioRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioService.save(dto, auth0UserId));
    }

    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<List<?>> getAllComentariosByUserId(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? comentarioService.findAllByUserId(userId)
                : comentarioService.findAllSimpleByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<?>> getAllComentariosByUser(@AuthenticationPrincipal Jwt jwt,
                                                           @RequestParam(defaultValue = "false") Boolean relations) {
        String auth0UserId = jwt.getSubject();
        List<?> response = relations
                ? comentarioService.findAllByUser(auth0UserId)
                : comentarioService.findAllSimpleByUser(auth0UserId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/routine/{routineId}")
    public ResponseEntity<List<ComentarioResponseDTO>> getComentariosByRoutineId(@PathVariable Long routineId) {
        return ResponseEntity.ok(comentarioService.findAllByRoutineId(routineId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComentarioSimpleDTO> updateComentarioContent(@AuthenticationPrincipal Jwt jwt,
                                                                       @PathVariable Long id,
                                                                       @Valid @RequestBody ComentarioContentRequestDTO dto) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(comentarioService.updateComentario(id, auth0UserId, dto));
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<ComentarioSimpleDTO> toggleLikeOnComment(@AuthenticationPrincipal Jwt jwt,
                                                                 @PathVariable Long id) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(comentarioService.toggleLikeOnComment(id, auth0UserId));

    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
        comentarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
