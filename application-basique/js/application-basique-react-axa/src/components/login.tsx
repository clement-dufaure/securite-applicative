import { Button } from "@mui/material";
import { useRecoilState } from "recoil";
import { useOidc, useOidcAccessToken } from "@axa-fr/react-oidc";

export const Login = () => {
  const { login, logout, renewTokens, isAuthenticated } = useOidc();
  const { accessToken, accessTokenPayload } = useOidcAccessToken();

  if (isAuthenticated) {
    console.log(accessTokenPayload);
    return (
      <Button
        variant="contained"
        color="primary"
        onClick={() => logout()}
      >
        Se déconnecter
      </Button>
    );
  } else {
    return (
      <>
        <Button
          variant="contained"
          color="primary"
          onClick={() => login()}
        >
          Se connecter
        </Button>
      </>
    );
  }
};
