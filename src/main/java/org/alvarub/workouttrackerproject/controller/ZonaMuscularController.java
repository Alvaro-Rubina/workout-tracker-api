package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;
import org.alvarub.workouttrackerproject.service.ZonaMuscularService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/muscle-groups")
@RequiredArgsConstructor
public class ZonaMuscularController {

    private final ZonaMuscularService zonaMuscularService;

    @PostMapping
    public ResponseEntity<ZonaMuscularResponseDTO> createZonaMuscular(@Valid @RequestBody ZonaMuscularRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(zonaMuscularService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getZonaMuscularById(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? zonaMuscularService.findById(id)
                : zonaMuscularService.findByIdSimple(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllZonasMusculares(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? zonaMuscularService.findAll()
                : zonaMuscularService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ZonaMuscularSimpleDTO> toggleZonaMuscularActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(zonaMuscularService.toggleActive(id));
    }

    // TODO: esto después ver si lo cambio a PatchMapping
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZonaMuscularSoft(@PathVariable Long id) {
        zonaMuscularService.softDelete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
