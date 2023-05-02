package com.entando.hub.catalog.service.model.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
