package org.alvarub.workouttrackerproject.service.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.SignupRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.SignupResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceAuth0 {

    @Value("${auth0.connection}")
    private String connection; /*Username-Password-Authentication*/

    private final ManagementAPI managementAPI;

    /**
     * Crea un usuario en Auth0 usando Management API
     */
    public SignupResponseDTO signup(SignupRequestDTO request) throws Auth0Exception {
        User user = new User(connection);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword().toCharArray());
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        try {
            log.info("Creando usuario {} en Auth0 vía Management API", request.getEmail());
            User createdUser = managementAPI.users().create(user).execute();

            return SignupResponseDTO.builder()
                    .userId(createdUser.getId())
                    .email(createdUser.getEmail())
                    .name(createdUser.getName())
                    .build();

        } catch (Auth0Exception e) {
            log.error("Error creando usuario en Auth0", e);
            throw e;
        }
    }

    /**
     * Activa o desactiva un usuario en Auth0
     * @param auth0UserId ID del usuario en Auth0 (sub)
     * @param active true para activar, false para desactivar
     */
    public void toggleActive(String auth0UserId, boolean active) throws Auth0Exception {
        try {
            User userUpdate = new User();
            userUpdate.setBlocked(!active); // blocked = true -> desactivado

            log.info("Actualizando estado activo del usuario Auth0 {}", auth0UserId);
            Request<User> request = managementAPI.users().update(auth0UserId, userUpdate);
            request.execute();
            log.info("Usuario Auth0 {} actualizado exitosamente", auth0UserId);

        } catch (Auth0Exception e) {
            log.error("Error actualizando estado activo del usuario Auth0 {}", auth0UserId, e);
            throw e; // propago para que el service que llama maneje la consistencia
        }
    }

    /**
     * Asigna un rol a un usuario en Auth0
     */
    public void setRole(String auth0UserId, String auth0RoleId) throws Auth0Exception {
        try {
            log.info("Asignando rol {} al usuario Auth0 {}", auth0RoleId, auth0UserId);
            managementAPI.users().addRoles(auth0UserId, Collections.singletonList(auth0RoleId)).execute();
            log.info("Rol asignado exitosamente al usuario Auth0 {}", auth0UserId);

        } catch (Auth0Exception e) {
            log.error("Error asignando rol al usuario Auth0 {}", auth0UserId, e);
            throw e;
        }
    }

    /**
     * Cambia la contraseña de un usuario en Auth0
     * @param auth0UserId ID del usuario en Auth0 (sub)
     * @param newPassword Nueva contraseña
     */
    public void changePassword(String auth0UserId, String newPassword) throws Auth0Exception {
        try {
            User userUpdate = new User();
            userUpdate.setPassword(newPassword.toCharArray());
            log.info("Cambiando contraseña del usuario Auth0 {}", auth0UserId);
            managementAPI.users().update(auth0UserId, userUpdate).execute();
            log.info("Contraseña cambiada exitosamente para el usuario Auth0 {}", auth0UserId);
        } catch (Auth0Exception e) {
            log.error("Error cambiando la contraseña del usuario Auth0 {}", auth0UserId, e);
            throw e;
        }
    }

    public RolResponseDTO getUserRol(String auth0UserId) {
        try {
            log.info("Obteniendo roles del usuario Auth0 {}", auth0UserId);
            // Obtiene los roles del usuario desde Auth0
            List<Role> roles = managementAPI.users().listRoles(auth0UserId, null).execute().getItems();
            if (roles != null && !roles.isEmpty()) {
                Role rol = roles.getFirst();
                return RolResponseDTO.builder()
                        .name(rol.getName())
                        .description(rol.getDescription())
                        .build();
            } else {
                log.warn("El usuario Auth0 {} no tiene roles asignados", auth0UserId);
                return null;
            }
        } catch (Auth0Exception e) {
            log.error("Error obteniendo roles del usuario Auth0 {}", auth0UserId, e);
            return null;
        }
    }

    public String getUserPictureUrl(String auth0UserId) {
        try {
            log.info("Obteniendo foto de perfil del usuario Auth0 {}", auth0UserId);
            User user = managementAPI.users().get(auth0UserId, null).execute();
            return user.getPicture();
        } catch (Auth0Exception e) {
            log.error("Error obteniendo foto de perfil del usuario Auth0 {}", auth0UserId, e);
            return null;
        }
    }
    /**
     * Elimina un usuario de Auth0
     */
    public void deleteUser(String auth0UserId) throws Auth0Exception {
        try {
            log.info("Eliminando usuario Auth0 {}", auth0UserId);
            managementAPI.users().delete(auth0UserId).execute();
            log.info("Usuario Auth0 {} eliminado exitosamente", auth0UserId);

        } catch (Auth0Exception e) {
            log.error("Error eliminando usuario Auth0 {}", auth0UserId, e);
            throw e;
        }
    }
}
