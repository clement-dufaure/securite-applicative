import React from 'react';
import Barre from './components/barre.jsx';
import ContenuJeton from './components/contenuJeton.jsx';
import UserInfo from './components/userinfo.jsx';

const App = () => {
  return (
    <>
      <Barre />
      <br />
      <ContenuJeton />
      <UserInfo />
    </>
  );
}

export default App;