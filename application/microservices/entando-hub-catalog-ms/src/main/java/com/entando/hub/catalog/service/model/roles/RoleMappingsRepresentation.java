package com.entando.hub.catalog.service.model.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoleMappingsRepresentation {
        @JsonProperty("clientMappings")
        private Map<String, ClientMapping> clientMappings;
}



