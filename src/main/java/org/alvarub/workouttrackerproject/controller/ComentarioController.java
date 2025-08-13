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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> createComentario(@Valid @RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioService.save(dto));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<?>> getComentariosByUserId(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? comentarioService.findAllByUserId(userId)
                : comentarioService.findAllSimpleByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/routines/{routineId}")
    public ResponseEntity<List<ComentarioResponseDTO>> getComentariosByRoutineId(@PathVariable Long routineId) {
        return ResponseEntity.ok(comentarioService.findAllByRoutineId(routineId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComentarioSimpleDTO> updateComentarioContent(@PathVariable Long id,
                                                                       @Valid @RequestBody ComentarioContentRequestDTO dto) {
        return ResponseEntity.ok(comentarioService.updateComentario(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
        comentarioService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

}
