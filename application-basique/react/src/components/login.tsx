import Button from '@material-ui/core/Button';
import Keycloak from 'keycloak-js';
import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { init } from '../redux/actions';

const Login = () => {
  var keycloak = useSelector(state => state.keycloak);

  const dispatch = useDispatch();

  if (!keycloak) {
    keycloak = Keycloak({
      url: 'http://localhost:8180/auth',
      realm: 'formation',
      clientId: 'localhost-frontend'
    });
    keycloak.init().then((authenticated) => {
      console.log(keycloak)
      console.log(authenticated)
      if (authenticated) {
        console.log(keycloak)
        dispatch(init(keycloak));
      }
    }).catch((err) => console.log(err));
  }

  if (keycloak && keycloak.authenticated) {
    return (<Button variant="contained" color="primary"
      onClick={
        () => keycloak.logout()
      }>Se déconnecter</Button>
    );
  } else {
    return (
      <>
        <Button variant="contained" color="primary"
          onClick={
            () =>
              keycloak.login()}
        >Se connecter</Button>
      </>
    );
  }
}




export default Login;