import React, {Component} from 'react'
import {
  authenticationChanged,
  getHigherRole,
  getUserName,
  isAuthenticated
} from "../../helpers/helpers"
import withKeycloak from "../../auth/withKeycloak"
import {HashRouter, Link} from "react-router-dom"
import {getPortalUserDetails} from "../../integration/api-adapters"
import {ADMIN} from "../../helpers/constants";
import i18n from '../../i18n';

import "./login.scss"

const KEYCLOAK_EVENT_ID = 'keycloak'

class Login extends Component {

constructor(props) {

    super(props)
    this.state = {
      loading: true,
      currentUserName: ""
    }
    this.keycloakEventHandler = this.keycloakEventHandler.bind(this)
 }

  keycloakEventHandler(event) {
    const keycloakEvent = event.detail.eventType
    const {keycloak} = this.props
    switch (keycloakEvent) {
        //Wait until keycloak is ready before displaying the nav elements
      case 'onReady':
        this.setState({
          loading: false
        })
        break
      case 'onAuthRefreshError':
        keycloak.logout()
        break
      default:
        break
    }
  }

  componentDidMount() {
    window.addEventListener(KEYCLOAK_EVENT_ID, this.keycloakEventHandler)
  }

  componentDidUpdate(prevProps) {

    const { config } = this.props
    const { systemParams } = config || {};
    const { api } = systemParams || {};
    const apiUrl = api && api['entando-hub-api'].url;

    if (authenticationChanged(this.props, prevProps)) {
      this.setState({
        loading: false,
      })
      getUserName().then(username => {
        this.setState({
          currentUserName: username,
        })

          getPortalUserDetails(apiUrl).then(portalUser => {
          this.setState({
            currentUserOrgName: portalUser
              && portalUser.organisations
              && portalUser.organisations[0]
              && portalUser.organisations[0].organisationName
          })
        })

      })
    }
  }



  componentWillUnmount() {
    window.removeEventListener(KEYCLOAK_EVENT_ID, this.keycloakEventHandler)
  }

  render() {
    const {keycloak} = this.props
    const loginUrl = window.location.origin + window.location.pathname;
    const handleLogin = () => keycloak.login({ redirectUri: loginUrl });
    const handleLogout = () => keycloak.logout({ redirectUri: loginUrl });

    if (!this.state.loading) {
      return (
          <div className="entando-eh-login">
          {!isAuthenticated(this.props) ? (
              <div className="log-button">
                <button className="log-in" onClick={handleLogin} title={"Login"}>
                  {"Login"}
                  <i className="fas fa-sign-in-alt"/>
                </button>
              </div>
          ) : (
              <>
                {(
                    <>
                      <div className="spacer">
                        {this.state.currentUserName}
                      </div>
                      |
                      <div className="spacer">
                        {this.state.currentUserOrgName}
                      </div>
                      {getHigherRole() === ADMIN &&
                      <div className="admin-page">
                        <HashRouter>
                          <Link to="/admin">Admin
                          </Link>
                        </HashRouter>
                        <i className="fas fa-cog"/>
                      </div>
                      }
                    </>
                )}
                <div className="log-button">
                   <button className="log-out" href="#" onClick={handleLogout}
                        // title={"Logout"}>
                        title={i18n.t('page.header.logout')}>
                   {i18n.t('page.header.logout')}<i className="fas fa-sign-out-alt"/>
                   </button>
                </div>
              </>
          )}
        </div>
      )
    } else {
      return null
    }
  }
}

export default withKeycloak(Login)
