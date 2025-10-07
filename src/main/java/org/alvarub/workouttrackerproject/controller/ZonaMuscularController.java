package org.alvarub.workouttrackerproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.dto.zonamuscular.ZonaMuscularUpdateRequestDTO;
import org.alvarub.workouttrackerproject.service.ZonaMuscularService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/muscle-groups")
@RequiredArgsConstructor
public class ZonaMuscularController {

    private final ZonaMuscularService zonaMuscularService;

    @PostMapping(value = "/admin", consumes = {"multipart/form-data"})
    public ResponseEntity<ZonaMuscularResponseDTO> createZonaMuscular(@Valid @RequestPart("data") ZonaMuscularRequestDTO dto,
                                                                      @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(zonaMuscularService.save(dto, image));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<Object> getZonaMuscularById(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? zonaMuscularService.findById(id, false)
                : zonaMuscularService.findByIdSimple(id, false);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getZonaMuscularByIdVerifyActive(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? zonaMuscularService.findById(id, true)
                : zonaMuscularService.findByIdSimple(id, true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<?>> getAllZonasMusculares(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? zonaMuscularService.findAll()
                : zonaMuscularService.findAllSimple();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllActiveZonasMusculares(@RequestParam(defaultValue = "false") Boolean relations) {
        List<?> response = relations
                ? zonaMuscularService.findAllActive()
                : zonaMuscularService.findAllSimpleActive();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/{id}/toggle-active")
    public ResponseEntity<ZonaMuscularSimpleDTO> toggleZonaMuscularActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(zonaMuscularService.toggleActive(id));
    }

    @PatchMapping(value = "/admin/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ZonaMuscularResponseDTO> updateZonaMuscular(@PathVariable Long id,
                                                                      @Valid @RequestPart("data") ZonaMuscularUpdateRequestDTO dto,
                                                                      @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(zonaMuscularService.update(id, dto, image));
    }

    @DeleteMapping("/admin/{id}/image")
    public ResponseEntity<ZonaMuscularResponseDTO> deleteZonaMuscularImage(@PathVariable Long id) {
        return ResponseEntity.ok(zonaMuscularService.removeImage(id));
    }

    @PatchMapping("/admin/{id}/deactivate")
    public ResponseEntity<ZonaMuscularSimpleDTO> deactivateZonaMuscular(@PathVariable Long id) {
        return ResponseEntity.ok(zonaMuscularService.softDelete(id));
    }

}
