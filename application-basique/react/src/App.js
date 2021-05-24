import React from 'react';
import Barre from './components/barre.tsx';
import ContenuJeton from './components/contenuJeton.tsx';
import UserInfo from './components/userinfo.tsx';

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