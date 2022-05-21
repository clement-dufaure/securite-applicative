import { Button } from "@mui/material";
import Keycloak from "keycloak-js";
import { useRecoilState } from "recoil";
import { keycloak } from "../recoil/store";

export const Login = () => {
  var [authentication, setAuthentication] = useRecoilState(keycloak);

  if (!authentication.clientId) {
    const kc = new Keycloak({
      url: "https://localhost:8081/auth",
      realm: "test",
      clientId: "localhost",
    });

    kc.init({})
      .then(() => setAuthentication(kc))
      .catch((e) => console.log(e));
  }

  if (authentication && authentication.authenticated) {
    return (
      <Button
        variant="contained"
        color="primary"
        onClick={() => authentication.logout()}
      >
        Se déconnecter
      </Button>
    );
  } else {
    console.log(authentication);
    return (
      <>
        <Button
          variant="contained"
          color="primary"
          onClick={() => authentication.login()}
        >
          Se connecter
        </Button>
      </>
    );
  }
};
