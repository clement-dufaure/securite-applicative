import { Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useRecoilValue } from "recoil";
import { getUserInfo } from "../recoil/api";
import { useOidcFetch, useOidc, useOidcAccessToken } from "@axa-fr/react-oidc";

export const UserInfo = () => {
  const [userInfo, setUserInfo] = useState("");
  const { login, logout, renewTokens, isAuthenticated } = useOidc();
  const { accessToken, accessTokenPayload } = useOidcAccessToken();
  const { fetch } = useOidcFetch();

  useEffect(() => {
    const apiUrl = "https://localhost/8081/auth/realms/test/protocol/openid-connect/userinfo";
    fetch(apiUrl).then((response)=>{
      response.json().then(res=>
        setUserInfo(JSON.stringify(res))
        )
    });
  }, []);

  if (
    isAuthenticated &&
    accessTokenPayload?.realm_access?.roles.includes("offline_access")
  ) {
    return (
      <>
        <Typography variant="h4" gutterBottom>
          User info
        </Typography>
        <Typography variant="body1">{userInfo}</Typography>
      </>
    );
  } else {
    return <p>Accès refusé</p>;
  }
};
