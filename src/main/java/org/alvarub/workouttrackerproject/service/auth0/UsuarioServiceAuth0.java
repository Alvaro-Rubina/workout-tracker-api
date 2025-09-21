package org.alvarub.workouttrackerproject.service.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvarub.workouttrackerproject.exception.UserRegistrationException;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.Auth0SignupRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.auth0.Auth0SignupResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceAuth0 {

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Value("${auth0.client.id}")
    private String clientId;

    @Value("${auth0.connection}")
    private String connection; /*Username-Password-Authentication*/

    private final ManagementAPI managementAPI;
    private final RestTemplate restTemplate = new RestTemplate();

    public Auth0SignupResponseDTO signup(Auth0SignupRequestDTO request) {
        String url = "https://" + auth0Domain + "/dbconnections/signup";

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());
        body.put("connection", connection);
        if (request.getName() != null) {
            body.put("name", request.getName());
        }

        try {
            log.info("Registrando usuario {} en Auth0 vía Authentication API", request.getEmail());
            return restTemplate.postForObject(url, body, Auth0SignupResponseDTO.class);
        } catch (Exception e) {
            log.error("Error registrando usuario {} en Auth0", request.getEmail(), e);
            throw new UserRegistrationException(e.getMessage());
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
