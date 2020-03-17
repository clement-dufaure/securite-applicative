import Button from '@material-ui/core/Button';
import Keycloak from 'keycloak-js';
import React, { Component } from 'react';
import { connect } from "react-redux";
import { init } from "../redux/actions.js";

class Login extends Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    if (!this.state.keycloakInitiated) {
      var keycloak = Keycloak(
        {
          url: 'https://auth.insee.test/auth',
          realm: 'agents-insee-interne',
          clientId: 'localhost-frontend'
        });

      //Pour un login automatique
      // keycloak.init({onLoad: 'login-required'}).success(() => {this.props.init(keycloak); });
      keycloak.init().success(() => {
        this.props.init(keycloak);
      }
      );
    }

  }

  render() {
    if (this.props.keycloak) {
      if (this.props.keycloak.authenticated) {
        return (<Button variant="contained" color="primary"
          onClick={() =>
            this.props.keycloak.logout({ redirectUri: window.location.protocol + "//" + window.location.host + process.env.PUBLIC_URL })
          }>Se déconnecter</Button>
        );
      } else {
        return (
          <div>
            <Button variant="contained" color="primary"
              onClick={() => {
                sessionStorage.setItem("realm", "agent");
                //this.props.keycloakAgent.login({ redirectUri: window.location.protocol + "//" + window.location.host + process.env.PUBLIC_URL });
                this.props.keycloak.login({ redirectUri: window.location.protocol + "//" + window.location.host + process.env.PUBLIC_URL });
              }}
            >Se connecter</Button>
          </div>
        );
      }
    } else {
      return (
        <div>...</div>
      );
    }
  }
}

const mapStateToProps = state => ({
  keycloak: state.keycloak
});
const mapDispatchToProps = { init };


export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);