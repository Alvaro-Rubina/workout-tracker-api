package org.alvarub.workouttrackerproject.config;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvarub.workouttrackerproject.persistence.entity.Categoria;
import org.alvarub.workouttrackerproject.persistence.entity.Rol;
import org.alvarub.workouttrackerproject.persistence.repository.CategoriaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.RolRepository;
import org.alvarub.workouttrackerproject.service.auth0.RolServiceAuth0;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import static org.alvarub.workouttrackerproject.utils.Constants.DEFAULT_CATEGORY_NAME;
import static org.alvarub.workouttrackerproject.utils.Constants.ROLES;

/**
 * Inicializador centralizado para datos por defecto de la aplicación.
 * - Crea la categoría por defecto si no existe.
 * - Crea/verifica los roles por defecto en DB y en Auth0.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartupDataInitializer {

    private final CategoriaRepository categoriaRepository;
    private final RolRepository rolRepository;
    private final RolServiceAuth0 rolServiceAuth0;

    @PostConstruct
    public void initDefaults() {
        initDefaultCategoria();
        initDefaultRoles();
    }

    // Categoria por defecto
    @Transactional
    public void initDefaultCategoria() {
        if (!categoriaRepository.existsByName(DEFAULT_CATEGORY_NAME)) {
            Categoria categoria = Categoria.builder()
                    .name(DEFAULT_CATEGORY_NAME)
                    .active(true)
                    .build();
            categoriaRepository.save(categoria);
            log.info("Categoría por defecto '{}' creada", DEFAULT_CATEGORY_NAME);
        } else {
            log.info("Categoría por defecto '{}' ya existe", DEFAULT_CATEGORY_NAME);
        }
    }

    // Roles por defecto (DB + Auth0)
    @Transactional
    public void initDefaultRoles() {
        log.info("Verificando que los roles por defecto existan en Auth0 y en base de datos...");
        ROLES.forEach((rolName, description) -> {
            try {
                ensureRoleExists(rolName, description);
            } catch (Auth0Exception e) {
                log.error("Error creando/verificando rol por defecto en Auth0: {}", rolName, e);
            }
        });
    }

    private void ensureRoleExists(String name, String description) throws Auth0Exception {
        Rol existingRol = rolRepository.findByNameIgnoreCase(name).orElse(null);

        if (existingRol != null) {
            // Verificar que exista también en Auth0
            log.debug("El rol '{}' ya existe en la base de datos. Verificando existencia en Auth0...", name);
            Role rolAuth0 = rolServiceAuth0.getRoleByName(existingRol.getName());

            if (rolAuth0 != null) {
                log.debug("El rol '{}' ya existe en Auth0.", name);
                return;
            }

            // Si se borró en Auth0, crearlo de nuevo y actualizar auth0RoleId y descripción
            log.warn("El rol '{}' existe en la base de datos pero no en Auth0. Creando en Auth0.", name);
            Role newRolAuth0 = rolServiceAuth0.createRol(name, description);

            existingRol.setAuth0RoleId(newRolAuth0.getId());
            existingRol.setDescription(newRolAuth0.getDescription());

            rolRepository.save(existingRol);
            log.info("Rol '{}' creado en Auth0 y actualizado en la base de datos.", name);
            return;
        }

        // No existe en DB: crearlo en Auth0 y persistir en DB
        log.info("El rol '{}' no existe en la base de datos. Creando en Auth0 y guardando en la base de datos...", name);
        Role rolAuth0 = rolServiceAuth0.createRol(name, description);

        Rol rol = Rol.builder()
                .name(rolAuth0.getName())
                .description(rolAuth0.getDescription())
                .auth0RoleId(rolAuth0.getId())
                .build();

        rolRepository.save(rol);
        log.info("Rol '{}' creado exitosamente en la base de datos.", name);
    }
}
