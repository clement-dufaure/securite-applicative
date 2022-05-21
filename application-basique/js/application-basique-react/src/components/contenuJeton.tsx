import {
  List,
  ListItem,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Typography,
} from "@mui/material";
import { useRecoilValue } from "recoil";
import { keycloak } from "../recoil/store";

const returnAllValuesLigne = (object: any) => {
  return Object.keys(object).map((key) => {
    if (typeof object[key] == "object") {
      return (
        <ListItem className="whiteSpaceNoWrap">
          <Typography variant="body1"> {key} :</Typography>
          <List component="nav">{returnAllValuesLigne(object[key])}</List>
        </ListItem>
      );
    } else {
      return (
        <ListItem className="whiteSpaceNoWrap">
          <Typography variant="body1">
            {" "}
            {key} : {object[key]}
          </Typography>
        </ListItem>
      );
    }
  });
};

const returnAllValues = (object: any) => {
  return Object.keys(object).map((key) => {
    if (typeof object[key] == "object") {
      return (
        <TableRow key={key}>
          <TableCell component="th" scope="row">
            {" "}
            {key}
          </TableCell>
          <TableCell>
            <Table>
              <TableBody>{returnAllValues(object[key])}</TableBody>
            </Table>
          </TableCell>
        </TableRow>
      );
    } else {
      return (
        <TableRow key={key}>
          <TableCell component="th" scope="row">
            {" "}
            {key}
          </TableCell>
          <TableCell>{object[key]}</TableCell>
        </TableRow>
      );
    }
  });
};

export const ContenuJeton = () => {
  const authentication = useRecoilValue(keycloak);
  if (authentication && authentication.authenticated) {
    return (
      <>
        <Typography variant="h4">Contenu du jeton</Typography>

        <Paper>
          <Table>
            <TableBody>{returnAllValues(authentication.tokenParsed)}</TableBody>
          </Table>
        </Paper>
      </>
    );
  } else return <p>Vous êtes anonyme</p>;
};
