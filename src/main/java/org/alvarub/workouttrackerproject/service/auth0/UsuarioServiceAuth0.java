package org.alvarub.workouttrackerproject.service.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.Auth0SignupRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.Auth0SignupResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
    public Auth0SignupResponseDTO signup(Auth0SignupRequestDTO request) throws Auth0Exception {
        User user = new User(connection);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword().toCharArray());
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        try {
            log.info("Creando usuario {} en Auth0 vía Management API", request.getEmail());
            User createdUser = managementAPI.users().create(user).execute();

            return Auth0SignupResponseDTO.builder()
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
