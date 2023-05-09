package com.entando.hub.catalog.rest.helpers;

import com.entando.hub.catalog.config.AuthoritiesConstants;
import com.entando.hub.catalog.service.model.roles.ClientMapping;
import com.entando.hub.catalog.service.model.roles.RoleMapping;
import com.entando.hub.catalog.service.model.roles.RoleMappingsRepresentation;

import java.util.Collections;
public class AppBuilderBundleControllerTestHelper {
   public static RoleMappingsRepresentation getMockRoleMappingsRepresentation(String clientName, boolean isAdmin) {
        return new RoleMappingsRepresentation().builder()
                .clientMappings(Collections.singletonMap(clientName, getMockClientMapping(clientName, isAdmin)))
                .build();
    }

    public static ClientMapping getMockClientMapping(String clientName, boolean isAdmin){
       return new ClientMapping().builder()
               .client(clientName)
               .mappings(Collections.singletonList(getMockRoleMapping(isAdmin)))
               .build();
    }

    public static RoleMapping getMockRoleMapping(boolean isAdmin){
        String roleName = isAdmin == true ? AuthoritiesConstants.ADMIN : AuthoritiesConstants.MANAGER;
        return new RoleMapping().builder().name(roleName).build();
    }
}
