# Entando Hub Bundle

## Overview
This is the parent repository for the Entando Hub project which currently contains two deployable sub-projects
* `application` which has the MFEs and microservices for the Entando Hub application
* `content` which has the non-compiled components such as pages, page templates, and fragments.

## Getting Started

### Deploy on Entando App

Move inside one of the two folders `application` and `content`, which contain the bundles.<br/>
You can build,deploy and install the bundles on Entando with commands of ent.

```
ent bundle pack
ent bundle publish
ent bundle deploy
ent bundle install
```

For more details check the readme of `application` and `content`.

## Configure Hub Registry in AppBuilder
This sections is useful to connect an existing Entando Hub.
From AppBuilder menu, you select `Hub`. At the top right, select `Select Registry` and `New Registry`.

You can choose Name and configure the url endpoint.

* example endpoint value for localhost:

```
http://localhost:8081/appbuilder/api
```

* example endpoint value for production: 

```
http://{{YOUR-HOSTNAME}}/entando-hub-application-{{BUNDLE-CODE}}/entando-hub-catalog-ms/appbuilder/api
```


## Use Entando Hub as a bundle from AppBuilder

You can deploy through ent using pre-built image from entando.

```
ent ecr deploy --repo=docker://registry.hub.docker.com/entando/entando-hub-application

ent ecr deploy --repo=docker://registry.hub.docker.com/entando/entando-hub-content
```

Install the bundle from AppBuilder GUI. <br><br>
Set up permissions to configure the service:

1. Login to your Keycloak instance as an admin.
2. Give at least one user the ability to manage the Hub by granting the `eh-admin` role. Assign the `eh-admin` role for the `pn-{{BUNDLE-ID}}-{{PLUGIN-ID}}-entandopsdh-entando-hub-catalog-ms-server` client.
3. Give the generated plugin client permission to manage users:
*  From the left sidebar, go to Clients and select client ID `pn-{{BUNDLE-ID}}-{{PLUGIN-ID}}-entandopsdh-entando-hub-catalog-ms-server`.
* Click the `Service Account` tab at the top of the page and select `realm-management` from the `Client Roles` field.
* Choose `realm-admin` from `Available Roles`. Click `Add selected`. It should appear as an `Assigned Role`.

Note: `BUNDLE-ID` and `PLUGIN-ID` are dynamic values that depend on the publishing url.


## History
* v3.1 Introduces the ability to [support private catalogs](https://developer.entando.com/v7.2/tutorials/solution/entando-hub.html#create-a-private-catalog) and converts the Hub codebase to the newer docker-based (v5) format. Entando 7.2 is required to make use of Hub private catalogs from within the AppBuilder
  * Note: the git-based bundles (https://github.com/entando-samples/entando-hub-application-bundle.git and https://github.com/entando-samples/entando-hub-content-bundle.git) are now no longer maintained but still available for older Entando versions.
* v2.1 Includes a number of new features including the ability to add the source URL for each bundle, added support for Docker bundle URLs (new in Entando 7.1), an expanded description field, and a number of usability and security fixes. 
* v1.x Can be installed in Entando 6.3.2 or higher and includes API-level integration with the Entando 7.0+ App Builder. See https://developer.entando.com/v7.1/tutorials/solution/entando-hub.html for detailed instructions on installing and using the Hub, including connecting an Entando App Builder to any Hub instance.
