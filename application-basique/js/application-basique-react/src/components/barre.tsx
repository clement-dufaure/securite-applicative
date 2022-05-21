import { AppBar, Toolbar, Typography } from "@mui/material";
import { useRecoilValue } from "recoil";
import { keycloak } from "../recoil/store";
import { Login } from "./login";

export const ButtonAppBar = () => {
  const authentication = useRecoilValue(keycloak);
  var messageConnecte = "";
  if (authentication && authentication.authenticated) {
    messageConnecte =
      "Connecté en tant que " +
      authentication.tokenParsed?.preferred_username +
      " (" +
      authentication.realm +
      ")";
  }
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" color="inherit">
          Démonstration Keycloak avec React
        </Typography>
        <Typography variant="h6" color="inherit">
          {messageConnecte}
        </Typography>
        <Login />
      </Toolbar>
    </AppBar>
  );
};
