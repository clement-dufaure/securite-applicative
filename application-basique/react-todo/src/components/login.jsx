import Button from '@material-ui/core/Button';
import React, { Component } from 'react';
import { connect } from "react-redux";
import { init } from "../redux/actions.js";

class Login extends Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
  }

  render() {
    if (this.props.keycloak) {
      if (this.props.keycloak.authenticated) {
        return (<Button variant="contained" color="primary"
          onClick="">Se déconnecter</Button>
        );
      } else {
        return (
          <div>
            <Button variant="contained" color="primary"
              onClick=""
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