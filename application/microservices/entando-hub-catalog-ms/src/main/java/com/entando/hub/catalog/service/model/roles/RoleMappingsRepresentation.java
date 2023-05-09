package com.entando.hub.catalog.service.model.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMappingsRepresentation {
        @JsonProperty("clientMappings")
        private Map<String, ClientMapping> clientMappings;
}



