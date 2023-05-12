package com.entando.hub.catalog.service.model.roles;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// https://www.keycloak.org/docs-api/18.0/rest-api/index.html#_clientmappingsrepresentation
public class ClientMapping {
    private String id;
    private String client;
    private List<RoleMapping> mappings;
}
