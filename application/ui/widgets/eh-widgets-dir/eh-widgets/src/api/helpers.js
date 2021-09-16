import {ADMIN, SUPPORT, CUSTOMER, PARTNER, DOMAIN, AUTHOR, MANAGER} from './constants';

export const getKeycloakToken = () => {
    if (window && window.entando && window.entando.keycloak && window.entando.keycloak.authenticated) {
        return window.entando.keycloak.token;
    }
    return '';
};

export const isAuthenticated = props => {
    const {keycloak} = props;
    return keycloak.initialized && keycloak.authenticated;
};

export const authenticationChanged = (props, prevProps) => {
    const authenticated = isAuthenticated(props);
    const changedAuth = prevProps.keycloak.authenticated !== authenticated;
    return authenticated && changedAuth;
};

export const isHubAdmin = () => {
    return hasKeycloakClientRole(ADMIN);
};

export const isHubAuthor = () => {
    return hasKeycloakClientRole(AUTHOR);
};

export const isHubManager = () => {
    return hasKeycloakClientRole(MANAGER);
};

export const isHubUser = () => {
    debugger
    return isHubAdmin() || isHubManager() || isHubAuthor()
};

export const getUserName = async () => {
    if (window.entando && window.entando.keycloak && window.entando.keycloak.loadUserInfo) {
        const userInfo = await window.entando.keycloak.loadUserInfo()
        return userInfo.preferred_username
    } else {
        return ""
    }
}


export const hasKeycloakClientRole = clientRole => {
    if (getKeycloakToken()) {
        const {resourceAccess} = window.entando.keycloak;
        if (resourceAccess) {
            for (const client in resourceAccess) {
                const item = resourceAccess[client];
                if (item.roles && item.roles.includes(clientRole)) {
                    // console.debug("Found role {} with client {} ", clientRole, client);
                    return true;
                }
            }
        }
    }
    return false;
};

export const getDefaultOptions = () => ({
    headers: new Headers({
        Authorization: `Bearer ${getKeycloakToken()}`,
        'Content-Type': 'application/json',
    }),
});

