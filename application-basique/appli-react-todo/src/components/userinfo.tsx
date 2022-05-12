import { Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useRecoilValue } from "recoil";
import { getUserInfo } from "../recoil/api";
import { keycloak } from "../recoil/store";

export const UserInfo = () => {
  const authentication = useRecoilValue(keycloak);
  const [userInfo, setUserInfo] = useState("");

  useEffect(() => {
    getUserInfo(authentication).then((response) => setUserInfo(response));
  }, [authentication]);

  if (
    authentication &&
    authentication.authenticated &&
    authentication.tokenParsed?.realm_access?.roles.includes("offline_access")
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
