import axios from "axios";

export const getUserInfo = async (accessToken: any) => {
    const response = getAxiosAuthenticated(accessToken).get(
      "https://localhost:8081/auth/realms/test/protocol/openid-connect/userinfo"
    );
    return JSON.stringify((await response).data);
};

const getAxiosAuthenticated = (accessToken: any) => {
  axios.interceptors.request.use(async (config: any) => {
    // await new Promise<void>((resolve, reject) => {
    //   authentication
    //     .updateToken(30)
    //     .then(() => {
    //       resolve();
    //     })
    //     .catch(() => reject());
    // });
    config.headers!["Authorization"] = "Bearer " + accessToken;
    return config;
  });
  return axios;
};
