import { AppBar, Toolbar, Typography } from "@mui/material";
import { useOidc, useOidcAccessToken } from "@axa-fr/react-oidc";
import { useRecoilValue } from "recoil";
import { Login } from "./login";

export const ButtonAppBar = () => {
  const { login, logout, renewTokens, isAuthenticated } = useOidc();
  const { accessToken, accessTokenPayload } = useOidcAccessToken();
  var messageConnecte = "";
  if (isAuthenticated) {
    var admin ="pas admin";
    if(accessTokenPayload.realm_access.roles?.includes("admin")){
      admin="admin";
    }
    messageConnecte =
      "Connecté en tant que " +
      accessTokenPayload?.preferred_username +
      " (" +
      admin +
      ")";
  }
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" color="inherit">
          Démonstration oidc axa avec React
        </Typography>
        <Typography variant="h6" color="inherit">
          {messageConnecte}
        </Typography>
        <Login />
      </Toolbar>
    </AppBar>
  );
};
