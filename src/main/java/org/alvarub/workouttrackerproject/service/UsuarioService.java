package org.alvarub.workouttrackerproject.service;

import com.auth0.exception.Auth0Exception;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.ExistingResourceException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.exception.UserRegistrationException;
import org.alvarub.workouttrackerproject.mapper.UsuarioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rol;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.UsuarioRepository;
import org.alvarub.workouttrackerproject.service.auth0.UsuarioServiceAuth0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.alvarub.workouttrackerproject.utils.Constants.ADMIN_ROL_NAME;
import static org.alvarub.workouttrackerproject.utils.Constants.USER_ROL_NAME;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioServiceAuth0 usuarioServiceAuth0;
    private final UsuarioMapper usuarioMapper;
    private final RolService rolService;

    @Transactional
    public UsuarioResponseDTO save(UsuarioRequestDTO dto, Jwt jwt) throws Auth0Exception {
        Usuario usuario = usuarioMapper.toEntity(dto);
        Rol rol = rolService.getRolByNameOrThrow(USER_ROL_NAME, true);

        usuario.setAuth0Id(jwt.getSubject());
        usuario.setRole(rol);

        try {
            // Seteo el rol también en auth0 antes de guardar en el repo
            usuarioServiceAuth0.setRole(jwt.getSubject(), rol.getAuth0RoleId());
            usuarioRepository.save(usuario);

        } catch (DataAccessException e) {
            // Si falla en la BD, elimino el usuario en Auth0
            usuarioServiceAuth0.deleteUser(jwt.getSubject());
            throw new UserRegistrationException("Error guardando usuario en la base de datos", e);

        } catch (Auth0Exception e) {
            usuarioServiceAuth0.deleteUser(jwt.getSubject());
            throw new UserRegistrationException("Error asignando rol en Auth0", e);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO saveAdmin(UsuarioRequestDTO dto, Jwt jwt) throws Auth0Exception {
        Usuario usuario = usuarioMapper.toEntity(dto);
        Rol rol = rolService.getRolByNameOrThrow(ADMIN_ROL_NAME, true);

        usuario.setAuth0Id(jwt.getSubject());
        usuario.setRole(rol);

        try {
            // Seteo el rol también en auth0 antes de guardar en el repo
            usuarioServiceAuth0.setRole(jwt.getSubject(), rol.getAuth0RoleId());
            usuarioRepository.save(usuario);

        } catch (DataAccessException e) {
            usuarioServiceAuth0.deleteUser(jwt.getSubject());
            throw new UserRegistrationException("Error guardando usuario en la base de datos", e);

        } catch (Auth0Exception e) {
            usuarioServiceAuth0.deleteUser(jwt.getSubject());
            throw new UserRegistrationException("Error asignando rol en Auth0", e);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = getUsuarioOrThrow (id, false);
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO getUsuarioFromToken(Jwt jwt) {
        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        return usuarioRepository.findByAuth0Id(auth0Id)
                .map(usuarioMapper::toResponseDTO)
                .orElseGet(() -> {
                    // Verificar si el email ya existe
                    if (usuarioRepository.existsByEmail(email)) {
                        throw new ExistingResourceException("El email ya está registrado con otro método de autenticación");
                    }

                    // Crear nuevo usuario
                    Usuario newUser = Usuario.builder()
                            .auth0Id(auth0Id)
                            .email(email)
                            .name(name)
                            .role(rolService.getRolByNameOrThrow(USER_ROL_NAME, true))
                            .active(true)
                            .build();

                    return usuarioMapper.toResponseDTO(usuarioRepository.save(newUser));
                });
    }

    @Transactional(readOnly = true)
    public UsuarioStatsDTO findStatsById(Long id) {
        Usuario usuario = getUsuarioOrThrow(id, false);
        return usuarioMapper.toStatsDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioStatsDTO> findAllStats() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toStatsDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO toggleActive(Long id, Jwt jwt) throws Auth0Exception {
        Usuario usuario = getUsuarioOrThrow(id, false);

        // Desactivar usuario: pasar rutinas a privadas
        if (usuario.getActive()) {
            usuario.getCreatedRoutines().forEach(rutina -> {
                rutina.setPreviousPublicState(rutina.getIsPublic());
                if (rutina.getIsPublic()) {
                    rutina.setIsPublic(false);
                }
            });
        }

        // Activar usuario: restaurar rutinas públicas anteriores
        else {
            usuario.getCreatedRoutines().forEach(rutina -> {
                if (Boolean.TRUE.equals(rutina.getPreviousPublicState())) {
                    rutina.setIsPublic(true);
                }
                rutina.setPreviousPublicState(null);
            });
        }

        // Actualizo tambien en auth0
        usuarioServiceAuth0.toggleActive(usuario.getAuth0Id(), usuario.getActive());

        // auditoria por ahora opcional
        /*
        if (jwt != null) {
            String adminAuth0Id = jwt.getSubject();
            log.info("Admin {} cambió el estado del usuario {}", adminAuth0Id, usuario.getId());
        }*/

        return usuarioMapper.toResponseDTO(usuario);
    }

    // Métodos auxiliares
    public Usuario getUsuarioOrThrow(Long id, boolean verifyActive) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario con el ID " + id + " no encontrado"));

        if (verifyActive && !usuario.getActive()) {
            throw new NotFoundException("Usuario el ID " + id + " inactivo");
        }

        return usuario;
    }
}
