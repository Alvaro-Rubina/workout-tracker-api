package org.alvarub.workouttrackerproject.service;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;
    private final RolServiceAuth0 rolServiceAuth0;

    // Método para guardar los roles predefinidos
    @PostConstruct
    @Transactional
    public void initDefaultRoles() throws Auth0Exception {
        ROLES.forEach((rol, description) -> {
            try {
                log.info("Verificando que los roles por defecto existan en Auth y en base de datos...");
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

        // Verifico si existe en la DB
        if (existingRol != null) {
            // En caso de que exista en la BD, verifico que exista tambien en Auth0
            log.info("El rol '{}' ya existe en la base de datos. Verificando existencia en Auth0.", name);
            Role rolAuth0 = rolServiceAuth0.getRoleByName(existingRol.getName());

            if (rolAuth0 != null) {
                log.info("El rol '{}' ya existe en Auth0.", name);
                return rolMapper.toResponseDTO(existingRol);

            } else {
                // Si se borró en Auth0, lo vulevo a crear y actualizo el auth0RoleId del rol en DB
                log.warn("El rol '{}' existe en la base de datos pero no en Auth0. Creando en Auth0.", name);
                Role newRolAuth0 = rolServiceAuth0.createRol(name, description);
                existingRol.setAuth0RoleId(newRolAuth0.getId());
                existingRol.setDescription(newRolAuth0.getDescription());
                log.info("Rol '{}' creado en Auth0 y actualizado en la base de datos.", name);
                return rolMapper.toResponseDTO(rolRepository.save(existingRol));
            }
        }

        // En caso de que no exista en DB
        log.info("El rol '{}' no existe en la base de datos. Creando en Auth0 y guardando en la base de datos.", name);
        Role rolAuth0 = rolServiceAuth0.createRol(name, description);

        // Guardar en la BD (sea nuevo o existente en Auth0)
        Rol rol = Rol.builder()
                .name(rolAuth0.getName())
                .description(rolAuth0.getDescription())
                .auth0RoleId(rolAuth0.getId())
                .build();

        log.info("Rol '{}' creado exitosamente en la base de datos", name);
        return rolMapper.toResponseDTO(rolRepository.save(rol));
    }

}
