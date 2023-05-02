package com.entando.hub.catalog.service.model.roles;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClientMapping {
    private String id;
    private String client;
    private List<RoleMapping> mappings;
}
