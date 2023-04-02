import axios from "axios";
import Keycloak from "keycloak-js";

export const getUserInfo = async (authentication: Keycloak) => {
  if (authentication.authenticated) {
    const response = await getAxiosAuthenticated(authentication).get(
      "https://localhost:8081/auth/realms/test/protocol/openid-connect/userinfo"
    );
    return JSON.stringify(response.data);
  } else {
    return "";
  }
};

const getAxiosAuthenticated = (authentication: Keycloak) => {
  axios.interceptors.request.use(async (config: any) => {
    await new Promise<void>((resolve, reject) => {
      authentication
        .updateToken(30)
        .then(() => {
          resolve();
        })
        .catch(() => reject());
    });
    config.headers!["Authorization"] = "Bearer " + authentication.token;
    return config;
  });
  return axios;
};
