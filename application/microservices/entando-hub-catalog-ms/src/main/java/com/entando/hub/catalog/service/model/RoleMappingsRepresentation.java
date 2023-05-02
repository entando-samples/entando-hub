package com.entando.hub.catalog.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
class ClientMapping {
    private String id;
    private String client;
    private List<RoleMapping> mappings;
}

@Getter
@Setter
class RoleMapping {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    @JsonProperty("clientRole")
    private boolean isClientRole;
    @JsonProperty("containerId")
    private String containerId;
}

@Getter
@Setter
@ToString
public class RoleMappingsRepresentation {
        @JsonProperty("clientMappings")
        private Map<String, ClientMapping> clientMappings;
}



