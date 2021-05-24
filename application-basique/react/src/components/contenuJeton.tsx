//import ListSubheader from '@material-ui/core/ListSubheader';
import { Typography } from '@material-ui/core';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
//import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import { useSelector } from 'react-redux';
import React from 'react';

const returnAllValuesLigne = (object) => {
  return Object.keys(object).map((key) => {
    if (typeof object[key] == "object") {
      return <ListItem className="whiteSpaceNoWrap">
        <Typography variant="body1"> {key} :</Typography>
        <List component="nav">{returnAllValuesLigne(object[key])}</List>
      </ListItem >
    } else {
      return <ListItem className="whiteSpaceNoWrap">
        <Typography variant="body1"> {key} : {object[key]}</Typography>
      </ListItem >
    }
  })
}

const returnAllValues = (object) => {
  return Object.keys(object).map((key) => {
    if (typeof object[key] == "object") {
      return <TableRow key={key}>
        <TableCell component="th" scope="row"> {key}</TableCell>
        <TableCell >
          <Table>
            <TableBody>
              {returnAllValues(object[key])}
            </TableBody>
          </Table>
        </TableCell>
      </TableRow>
    } else {
      return <TableRow key={key}>
        <TableCell component="th" scope="row"> {key}</TableCell>
        <TableCell >{object[key]}</TableCell>
      </TableRow>
    }
  })
}

const ContenuJeton = () => {
  const keycloak = useSelector(state => state.keycloak);
  if (keycloak && keycloak.authenticated) {
    return (<>
      <Typography variant="h4">Contenu du jeton</Typography>

      <Paper >
        <Table >
          <TableBody>
            {returnAllValues(keycloak.tokenParsed)}
          </TableBody>
        </Table>
      </Paper>
    </>
    );
  }
  else return (<p>Vous êtes anonyme</p>);
}

export default ContenuJeton;