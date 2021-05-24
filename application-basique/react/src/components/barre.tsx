import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import React from 'react';
import { useSelector } from "react-redux";
import Login from './login.jsx';

const ButtonAppBar = () => {
  const keycloak = useSelector(state => state.keycloak);
  var messageConnecte = "";
  if (keycloak && keycloak.authenticated) {
    messageConnecte = "Connecté en tant que " + keycloak.tokenParsed.preferred_username + " (" + keycloak.realm + ")";
  }
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" color="inherit" >
          Démonstration Keycloak avec React
            </Typography>
        <Typography variant="h6" color="inherit">
          {messageConnecte}
        </Typography>
        <Login />
      </Toolbar>
    </AppBar>
  );
}

export default ButtonAppBar;