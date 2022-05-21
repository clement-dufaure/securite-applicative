import "./App.css";
import { ButtonAppBar } from "./components/barre";
import { ContenuJeton } from "./components/contenuJeton";
import { UserInfo } from "./components/userinfo";

const App = () => {
  return (
    <>
      <ButtonAppBar />
      <br />
      <ContenuJeton />
      <UserInfo />
    </>
  );
};

export default App;
