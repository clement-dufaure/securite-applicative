import { Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useOidcFetch, useOidc, useOidcAccessToken } from "@axa-fr/react-oidc";

export const UserInfo = () => {
  const [userInfo, setUserInfo] = useState("");
  const { isAuthenticated } = useOidc();
  const { accessTokenPayload } = useOidcAccessToken();
  const { fetch } = useOidcFetch();

  useEffect(() => {
    const apiUrl = "http://localhost:8180/realms/test/protocol/openid-connect/userinfo";
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
