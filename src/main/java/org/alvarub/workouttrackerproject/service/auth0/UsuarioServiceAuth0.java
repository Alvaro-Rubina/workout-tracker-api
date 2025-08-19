package org.alvarub.workouttrackerproject.service.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UsuarioServiceAuth0 {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceAuth0.class);

    private final ManagementAPI managementAPI;

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
