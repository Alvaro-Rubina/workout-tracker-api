package org.alvarub.workouttrackerproject.utils;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    public static final String DEFAULT_CATEGORY_NAME = "General";

    public static String DEFAULT_PFP = "https://static.vecteezy.com/system/resources/previews/013/360/247/non_2x/default-avatar-photo-icon-social-media-profile-sign-symbol-vector.jpg";

    public static final String USER_ROL_NAME = "USUARIO";
    public static final String ADMIN_ROL_NAME = "ADMIN";
    public static final String OWNER_ROL_NAME = "PROPIETARIO";

    public static final Map<String, String> ROLES = new HashMap<>() {{
        put(OWNER_ROL_NAME, "Rol con permisos absoultos para propietarios");
        put(ADMIN_ROL_NAME, "Rol con permisos extendidos para administradores.");
        put(USER_ROL_NAME, "Rol con permisos limitados para usuarios.");
    }};
}
