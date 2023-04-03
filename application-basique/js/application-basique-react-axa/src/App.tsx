import "./App.css";
import { ButtonAppBar } from "./components/barre";
import { ContenuJeton } from "./components/contenuJeton";
import { UserInfo } from "./components/userinfo";
import { OidcProvider } from "@axa-fr/react-oidc";

const configuration = {
  client_id: "localhost-frontend",
  redirect_uri: window.location.origin + "/authentication/callback",
  silent_redirect_uri:
    window.location.origin + "/authentication/silent-callback",
  scope: "openid profile email offline_access", // offline_access scope allow your client to retrieve the refresh_token
  authority: "http://localhost:8180/realms/test",
};

const App = () => {
  return (
    <OidcProvider configuration={configuration}>
      <ButtonAppBar />
      <br />
      <ContenuJeton />
      <UserInfo />
    </OidcProvider>
  );
};

export default App;
