package org.alvarub.workouttrackerproject.service;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.RolMapper;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rol;
import org.alvarub.workouttrackerproject.persistence.repository.RolRepository;
import org.alvarub.workouttrackerproject.service.auth0.RolServiceAuth0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.alvarub.workouttrackerproject.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class RolService {

    private static final Logger log = LoggerFactory.getLogger(RolService.class);

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;
    private final RolServiceAuth0 rolServiceAuth0;

    // Método para guardar los roles predefinidos
    @PostConstruct
    @Transactional
    public void initDefaultRoles() throws Auth0Exception {
        ROLES.forEach((rol, description) -> {
            try {
                createRoleIfNotExists(rol, description);
            } catch (Auth0Exception e) {
                log.error("Error creando rol por defecto en Auth0: {}", rol, e);
            }
        });
    }

    @Transactional
    public RolResponseDTO save(RolRequestDTO dto) throws Auth0Exception {
        return createRoleIfNotExists(dto.getName(), dto.getDescription());
    }

    @Transactional(readOnly = true)
    public RolResponseDTO findById(Long id) {
        Rol rol = getRolOrThrow(id, false);
        return rolMapper.toResponseDTO(rol);
    }

    @Transactional(readOnly = true)
    public RolResponseDTO findByName(String name) {
        Rol rol = getRolByNameOrThrow(name, false);
        return rolMapper.toResponseDTO(rol);
    }

    @Transactional(readOnly = true)
    public List<RolResponseDTO> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toResponseDTO)
                .toList();
    }

    // Métodos auxiliares
    public Rol getRolOrThrow(Long id, boolean verifyActive) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol con el id " + id + " no encontrado"));

        if (verifyActive && !rol.getActive()) {
            throw new NotFoundException("Rol el ID " + id + " inactivo");
        }

        return rol;
    }

    public Rol getRolByNameOrThrow(String name, boolean verifyActive) {
        Rol rol = rolRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));

        if (verifyActive && !rol.getActive()) {
            throw new NotFoundException("Rol inactivo");
        }

        return rol;
    }

    private RolResponseDTO createRoleIfNotExists(String name, String description) throws Auth0Exception {
        Rol existingRol = rolRepository.findByName(name).orElse(null);

        if (existingRol != null) {
            return rolMapper.toResponseDTO(existingRol);
        }

        Role rolAuth0 = rolServiceAuth0.createRol(name, description);

        Rol rol = Rol.builder()
                .name(name)
                .description(description)
                .auth0RoleId(rolAuth0.getId())
                .build();

        return rolMapper.toResponseDTO(rolRepository.save(rol));
    }
}
