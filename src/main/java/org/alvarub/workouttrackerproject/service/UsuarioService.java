package org.alvarub.workouttrackerproject.service;

import com.auth0.exception.Auth0Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvarub.workouttrackerproject.exception.ExistingResourceException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.exception.UserRegistrationException;
import org.alvarub.workouttrackerproject.mapper.UsuarioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioUpdateRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.SignupResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.SignupRequestDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rol;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.UsuarioRepository;
import org.alvarub.workouttrackerproject.service.auth0.UsuarioServiceAuth0;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.alvarub.workouttrackerproject.utils.Constants.ADMIN_ROL_NAME;
import static org.alvarub.workouttrackerproject.utils.Constants.USER_ROL_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioServiceAuth0 usuarioServiceAuth0;
    private final UsuarioMapper usuarioMapper;
    private final RolService rolService;

    @Transactional
    public UsuarioResponseDTO registerUser(String auth0UserId, String auth0UserEmail, String auth0UserName) throws Auth0Exception {
        Rol rol = rolService.getRolByNameOrThrow(USER_ROL_NAME, true);

        Usuario usuario = Usuario.builder()
                .name(auth0UserEmail)
                .auth0Id(auth0UserId)
                .email(auth0UserEmail)
                .name(auth0UserName)
                .role(rol)
                .build();

        log.info("Creando administrador {}", usuario.getEmail());

        try {
            // Seteo el rol en Auth0 antes de guardar en BD
            log.info("Asignando rol en Auth0 al administrador {}", usuario.getAuth0Id());
            usuarioServiceAuth0.setRole(auth0UserId, rol.getAuth0RoleId());

            log.info("Guardando administrador en base de datos {}", usuario.getEmail());
            usuarioRepository.save(usuario);
            log.info("Administrador {} creado exitosamente", usuario.getEmail());

        } catch (DataAccessException e) {
            log.error("Error guardando administrador en BD, eliminando usuario en Auth0 {}", usuario.getAuth0Id(), e);
            usuarioServiceAuth0.deleteUser(auth0UserId);
            throw new UserRegistrationException("Error guardando usuario en la base de datos", e);

        } catch (Auth0Exception e) {
            log.error("Error asignando rol en Auth0, eliminando usuario {}", usuario.getAuth0Id(), e);
            usuarioServiceAuth0.deleteUser(auth0UserId);
            throw new UserRegistrationException("Error asignando rol en Auth0", e);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO registerAdmin(SignupRequestDTO dto) throws Auth0Exception {
        Rol rol = rolService.getRolByNameOrThrow(ADMIN_ROL_NAME, true);

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ExistingResourceException("El email ya está registrado en la base de datos");
        }

        SignupResponseDTO auth0User = usuarioServiceAuth0.signup(dto);

        Usuario usuario = Usuario.builder()
                .auth0Id(auth0User.getUserId())
                .email(auth0User.getEmail())
                .name(auth0User.getName() != null ? auth0User.getName() : dto.getEmail())
                .role(rol)
                .build();

        log.info("Creando usuario {} con rol {}", usuario.getEmail(), rol.getName());

        try {
            // Asignar rol en Auth0
            log.info("Asignando rol en Auth0 al usuario {}", usuario.getAuth0Id());
            usuarioServiceAuth0.setRole(auth0User.getUserId(), rol.getAuth0RoleId());

            // Guardar en BD
            usuarioRepository.save(usuario);
            log.info("Usuario {} creado exitosamente", usuario.getEmail());

        } catch (DataAccessException e) {
            log.error("Error guardando usuario en BD, eliminando usuario en Auth0 {}", usuario.getAuth0Id(), e);
            usuarioServiceAuth0.deleteUser(auth0User.getUserId());
            throw new UserRegistrationException("Error guardando usuario en la base de datos", e);

        } catch (Auth0Exception e) {
            log.error("Error asignando rol en Auth0, eliminando usuario {}", usuario.getAuth0Id(), e);
            usuarioServiceAuth0.deleteUser(auth0User.getUserId());
            throw new UserRegistrationException("Error asignando rol en Auth0", e);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = getUsuarioOrThrow(id, false);

        UsuarioResponseDTO response = usuarioMapper.toResponseDTO(usuario);

        if (!usuario.getBodyWeightHistorial().isEmpty()) {
            response.setBodyWeight(usuario.getBodyWeightHistorial().getLast().getBodyWeight());
        }

        return response;
    }

    @Transactional
    public UsuarioResponseDTO getCurrentUsuario(String authoUserID, String auth0UserEmail, String auth0UserName) {

        return usuarioRepository.findByAuth0Id(authoUserID)
                .map(usuario -> {
                    UsuarioResponseDTO response = usuarioMapper.toResponseDTO(usuario);

                    if (!usuario.getBodyWeightHistorial().isEmpty()) {
                        response.setBodyWeight(usuario.getBodyWeightHistorial().getLast().getBodyWeight());
                    }

                    return response;
                })
                .orElseGet(() -> {
                    // Verifico si el email ya existe
                    if (usuarioRepository.existsByEmail(auth0UserEmail)) {
                        throw new ExistingResourceException("El email ya está registrado con otro método de autenticación");
                    }

                    Rol rolUsuario = rolService.getRolByNameOrThrow(USER_ROL_NAME, true);

                    // Creo nuevo usuario
                    Usuario newUser = Usuario.builder()
                            .auth0Id(authoUserID)
                            .email(auth0UserEmail)
                            .name(auth0UserName)
                            .role(rolUsuario)
                            .active(true)
                            .build();

                    log.info("Creando usuario desde token {}", auth0UserEmail);

                    try {
                        // Asigno el rol al usuario ya existente en Auth0
                        log.info("Asignando rol en Auth0 al usuario {}", authoUserID);
                        usuarioServiceAuth0.setRole(authoUserID, rolUsuario.getAuth0RoleId());

                        // Guardo en db
                        log.info("Guardando usuario en base de datos {}", auth0UserEmail);
                        Usuario saved = usuarioRepository.save(newUser);
                        return usuarioMapper.toResponseDTO(saved);

                    } catch (Auth0Exception e) {
                        log.error("Error asignando rol en Auth0 al usuario {}", authoUserID, e);
                        throw new UserRegistrationException("Error asignando rol en Auth0", e);

                    } catch (DataAccessException e) {
                        log.error("Error guardando usuario en BD {}", auth0UserEmail, e);
                        throw new UserRegistrationException("Error guardando usuario en la base de datos", e);
                    }
                });
    }

    @Transactional(readOnly = true)
    public UsuarioStatsDTO findStatsById(Long id) {
        Usuario usuario = getUsuarioOrThrow(id, false);

        UsuarioStatsDTO response = usuarioMapper.toStatsDTO(usuario);

        return response;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAllByRolName(String rolName) {
        Rol rol = rolService.getRolByNameOrThrow(rolName, true);

        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getRole().equals(rol))
                .map(usuario -> {
                    UsuarioResponseDTO response = usuarioMapper.toResponseDTO(usuario);

                    if (!usuario.getBodyWeightHistorial().isEmpty()) {
                        response.setBodyWeight(usuario.getBodyWeightHistorial().getLast().getBodyWeight());
                    }

                    return response;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioStatsDTO> findAllStats() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toStatsDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO toggleActive(Long id) throws Auth0Exception {
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

        log.info("Actualizando estado activo del usuario {} a {}", usuario.getEmail(), !usuario.getActive());
        usuarioServiceAuth0.toggleActive(usuario.getAuth0Id(), usuario.getActive());

        UsuarioResponseDTO response = usuarioMapper.toResponseDTO(usuario);

        if (!usuario.getBodyWeightHistorial().isEmpty()) {
            response.setBodyWeight(usuario.getBodyWeightHistorial().getLast().getBodyWeight());
        }
        return response;
    }

    @Transactional
    public UsuarioResponseDTO update(String auth0UserId, UsuarioUpdateRequestDTO dto) throws Auth0Exception {
        Usuario usuario = getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        if ((!usuario.getName().equals(dto.getName()) && (dto.getName() != null && !dto.getName().isBlank()))) {
            usuario.setName(dto.getName());
        }

        if ((!usuario.getPicture().equals(dto.getPicture()) && (dto.getPicture() != null && !dto.getPicture().isBlank()))) {
            usuario.setPicture(dto.getPicture());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuarioServiceAuth0.changePassword(usuario.getAuth0Id(), dto.getPassword());
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = getUsuarioOrThrow(id, true);
        // TODO: Delete, quizas poner un schedule para que se elimine dentro de 30 dias
    }

    // Métodos auxiliares
    public Usuario getUsuarioOrThrow(Long id, boolean verifyActive) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario con el ID " + id + " no encontrado"));

        if (verifyActive && !usuario.getActive()) {
            throw new NotFoundException("Usuario con el ID " + id + " inactivo");
        }

        return usuario;
    }

    public Usuario getUsuarioByAuth0IdOrThrow(String auth0UserId, boolean verifyActive) {
        Usuario usuario = usuarioRepository.findByAuth0Id(auth0UserId)
                .orElseThrow(() -> new NotFoundException("Usuario con el auth0Id proporcionado no encontrado"));

        if (verifyActive && !usuario.getActive()) {
            throw new NotFoundException("Usuario con el auth0Id proporcionado inactivo");
        }

        return usuario;
    }

}
