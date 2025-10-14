package org.alvarub.workouttrackerproject.controller;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.sesioncompletada.SesionCompletadaResponseDTO;
import org.alvarub.workouttrackerproject.service.SesionCompletaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/completed-sessions")
@RequiredArgsConstructor
public class SesionCompletadaController {

    private final SesionCompletaService sesionCompletaService;

    @GetMapping
    public List<SesionCompletadaResponseDTO> getAllUserCompletedSessions(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return sesionCompletaService.findAll(auth0UserId);
    }
}
