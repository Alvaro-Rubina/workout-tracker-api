package org.alvarub.workouttrackerproject.service.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UsuarioServiceAuth0 {

    private final ManagementAPI managementAPI;

    /**
     * Activa o desactiva un usuario en Auth0
     * @param auth0UserId ID del usuario en Auth0 (sub)
     * @param active true para activar, false para desactivar
     */
    public void toggleActive(String auth0UserId, boolean active) throws Auth0Exception {
        User userUpdate = new User();
        userUpdate.setBlocked(!active); // blocked = true -> desactivado

        Request<User> request = managementAPI.users().update(auth0UserId, userUpdate);
        request.execute(); // realiza la actualización
    }

    public void setRole(String auth0UserId, String auth0RoleId) throws Auth0Exception {
        managementAPI.users().addRoles(auth0UserId, Collections.singletonList(auth0RoleId)).execute();
    }

    public void deleteUser(String auth0UserId) throws Auth0Exception {
        managementAPI.users().delete(auth0UserId).execute();
    }
}
