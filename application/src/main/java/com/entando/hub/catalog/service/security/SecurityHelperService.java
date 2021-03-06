package com.entando.hub.catalog.service.security;

import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

@Service
public class SecurityHelperService {
    private final PortalUserRepository portalUserRepository;

    public SecurityHelperService(PortalUserRepository portalUserRepository) {
        this.portalUserRepository = portalUserRepository;
    }

    public Boolean userIsInTheOrganisation(Long organisationId) {
        String preferredUsername = ((KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();
        PortalUser portalUser = portalUserRepository.findByUsername(preferredUsername);
        return portalUser != null && portalUser.getOrganisations() != null && portalUser.getOrganisations().stream().anyMatch(organisation -> organisation.getId().equals(organisationId));
    }

    public Set<String> getUserRoles(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).map(this::getAuthorityFromRole).collect(Collectors.toSet());
    }

    private String getRoleFromAuthority(String authority) {
        return authority.substring(authority.indexOf("_")+1);
    }

    private String getAuthorityFromRole(String role){
        return "ROLE_"+role;
    }

    public Boolean hasRoles(Set<String> roles){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).map(this::getRoleFromAuthority).anyMatch(roles::contains);
    }

    //TRUE if user is not admin AND doesn't belong to the organisation
    public Boolean userIsNotAdminAndDoesntBelongToOrg(String organisationId) {
        Boolean isAdmin = hasRoles(Set.of(ADMIN));
        if (isAdmin) {
            return false;
        }
        return !userIsInTheOrganisation(Long.valueOf(organisationId));

    }

}
