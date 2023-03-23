import "./index.scss"
import "./App-carbon.scss"
import {HashRouter, Route, Switch} from "react-router-dom"
import CatalogPage from "./page/catalog/CatalogPage"
import BundleGroupPage from "./page/bundle-group/BundleGroupPage"
import UserManagementPage from "./page/user-management/UserManagementPage"
import OrganisationManagementPage from "./page/organisation-management/OrganisationManagementPage"
import CategoryManagementPage from "./page/category-management/CategoryManagementPage"
import RouteWithGate from "./components/routing/RouteWithGate";
import {isHubAdmin} from "./helpers/helpers";
import NotificationDispatcher from "./components/notification/NotificationDispatcher"
import i18n from "./i18n"
import BundleGroupVersionsPage from "./page/bundle-group-version/bg-version-catalog/BundleGroupVersionsPage"
import { useState } from "react"
import ApiKeyManagementPage from "./page/api-key-management/ApiKeyManagementPage"
import NotFound from "./components/errors/NotFound"
import { CatalogProvider } from "./contexts/CatalogContext"

function AppCarbon() {
  const [versionSearchTerm, setVersionSearchTerm] = useState('');

  return (
    <>
      <NotificationDispatcher />
      <HashRouter>
        <Switch>
          <Route path="/bundlegroup/:id" exact render={(props) => <BundleGroupPage {...props}/>}/>
          <Route path="/bundlegroup/versions/:id" render={(props) => <BundleGroupPage {...props}/>}/>
          <Route
            path="/versions/:id/:categoryId"
            render={(props) =>
              <CatalogProvider>
                <BundleGroupVersionsPage setVersionSearchTerm={setVersionSearchTerm} {...props}/>
              </CatalogProvider>
            }
          />
          <Route path="/catalog/:catalogId/bundlegroup/versions/:id" render={(props) => <BundleGroupPage {...props} />} />
          <Route path="/catalog/:catalogId/bundlegroup/:id" render={(props) => <BundleGroupPage {...props} />} />
          <Route path="/apikeys" render={(props) => <ApiKeyManagementPage {...props} />} />
          <RouteWithGate gateFunction={isHubAdmin} path="/admin*" component={UserManagementPage} />
          <RouteWithGate gateFunction={isHubAdmin} path="/organisations*" component={OrganisationManagementPage} />
          <RouteWithGate gateFunction={isHubAdmin} path="/organisation*" component={OrganisationManagementPage} />
          <RouteWithGate gateFunction={isHubAdmin} path="/categories*" component={CategoryManagementPage} />
          <RouteWithGate gateFunction={isHubAdmin} path="/category*" component={CategoryManagementPage} />
          <Route path="/404">
            <NotFound />
          </Route>
          <Route path="**/unauthorized">
            {i18n.t('page.unauthorized')}
          </Route>
          <Route
            path="/catalog/:catalogId/"
            render={(props) =>
              <CatalogProvider>
                <CatalogPage {...props} />
              </CatalogProvider>
            }
          />
          <Route
            path="**/"
            render={(props) =>
              <CatalogProvider>
                <CatalogPage setVersionSearchTerm={setVersionSearchTerm} versionSearchTerm={versionSearchTerm} {...props} />
              </CatalogProvider>
            }
          />
        </Switch>
      </HashRouter>
    </>
  )
}

export default AppCarbon
