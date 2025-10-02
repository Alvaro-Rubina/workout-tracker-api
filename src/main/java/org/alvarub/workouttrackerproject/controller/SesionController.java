package org.alvarub.workouttrackerproject.controller;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.service.SesionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @GetMapping("/admin/{id}")
    public ResponseEntity<Object> getSesionById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") Boolean relations) {
        Object response = relations
                ? sesionService.findById(id)
                : sesionService.findByIdSimple(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSesionByIdVisibleToUser(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable Long id,
                                                             @RequestParam(defaultValue = "false") Boolean relations) {
        String auth0UserId = jwt.getSubject();
        Object response = relations
                ? sesionService.findByIdVisibleToUser(id, auth0UserId)
                : sesionService.findByIdSimpleVisibleToUser(id, auth0UserId);
        return ResponseEntity.ok(response);
    }
}
