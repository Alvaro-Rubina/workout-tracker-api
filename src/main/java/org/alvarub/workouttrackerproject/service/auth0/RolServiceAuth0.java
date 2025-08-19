package org.alvarub.workouttrackerproject.service.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolRequestDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolServiceAuth0 {

    private final ManagementAPI managementAPI;

    public Role createRol(String name, String description) throws Auth0Exception {
        Role rolAuth0 = new Role();
        rolAuth0.setName(name);
        rolAuth0.setDescription(description);
        managementAPI.roles().create(rolAuth0).execute();
        return rolAuth0;
    }

}