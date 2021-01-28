import Button from '@material-ui/core/Button';
import React from 'react';
import { useSelector } from 'react-redux';

const Login = () => {
  var keycloak = useSelector(state => state.keycloak);

  if (keycloak && keycloak.authenticated) {
    return (<Button variant="contained" color="primary"
      onClick={
        () =>
          console.log("click !")}>Se déconnecter</Button>
    );
  } else {
    return (
      <>
        <Button variant="contained" color="primary"
          onClick={
            () =>
              console.log("click !")}
        >Se connecter</Button>
      </>
    );
  }
}




export default Login;