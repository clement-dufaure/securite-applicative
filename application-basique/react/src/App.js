import React, { Component } from 'react';
import Barre from './components/barre.jsx';
import ContenuJeton from './components/contenuJeton.jsx';
import Fonction from './components/fonction.jsx';
import UserInfo from './components/userinfo.jsx';
import Token from './components/token.jsx';
import { connect } from "react-redux";
import {
  BrowserRouter as Router,
  Route,
  Switch,
  Redirect
} from "react-router-dom";

var specifierComposantFonction = function (composant) {
  return (
    () => <Fonction Composant={composant} />
  )
}


class App extends Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <Router basename={process.env.PUBLIC_URL}>
        <div>
          <Barre />
          <br />
          <Switch>
            <Route
              path="/tokenParsed"
              component={specifierComposantFonction(ContenuJeton)}
            />
            <Route path="/scope" component={specifierComposantFonction(UserInfo)} />
            <Route path="/token" component={specifierComposantFonction(Token)} />
            <Route path="" render={() => <Redirect to="/tokenParsed" />} />

          </Switch>
        </div>
      </Router>
    );
  }

}


const mapStateToProps = state => ({
  keycloak: state.keycloak
});


export default connect(
  mapStateToProps,
)(App);