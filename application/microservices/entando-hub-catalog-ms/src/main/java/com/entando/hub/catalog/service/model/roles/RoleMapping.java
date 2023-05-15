package com.entando.hub.catalog.service.model.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// https://www.keycloak.org/docs-api/18.0/rest-api/index.html#_rolerepresentation
public class RoleMapping {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    @JsonProperty("clientRole")
    private boolean isClientRole;
    @JsonProperty("containerId")
    private String containerId;
}
