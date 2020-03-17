import React, { Component } from 'react';
import { connect } from "react-redux";

class FonctionAuthentifiee extends Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    if (this.props.keycloak && this.props.keycloak.authenticated) {
      var Composant = this.props.Composant;
      return (
        <Composant />
      )
    }
    else return (<p>Vous devez être authentifié pour accéder à cette fonctionnalité #401 #AuthenticationRequired</p>);
  }
}


const mapStateToProps = state => ({
  keycloak: state.keycloak
});


export default connect(
  mapStateToProps,
)(FonctionAuthentifiee);