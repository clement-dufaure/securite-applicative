import React, { Component } from 'react';
import { connect } from "react-redux";
import { withStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
//import ListSubheader from '@material-ui/core/ListSubheader';
import { Typography } from '@material-ui/core';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
//import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

const styles = theme => ({
  palette: {
    type: 'dark',
  },
  root: {
    width: '100%',
    marginTop: theme.spacing.unit * 3,
    overflowX: 'auto',
  },
  nested: {
    paddingLeft: theme.spacing.unit * 4,
  },
  table: {
    minWidth: 700,
  }
});

class ContenuJeton extends Component {

  constructor(props) {
    super(props);
    this.state = {
      open: true,
    };
  }

  returnAllValuesLigne(object) {
    return Object.keys(object).map((key) => {
      if (typeof object[key] == "object") {
        return <ListItem className="whiteSpaceNoWrap">
          <Typography variant="body1"> {key} :</Typography>
          <List component="nav">{this.returnAllValuesLigne(object[key])}</List>
        </ListItem >
      } else {
        return <ListItem className="whiteSpaceNoWrap">
          <Typography variant="body1"> {key} : {object[key]}</Typography>
        </ListItem >
      }
    })
  }

  returnAllValues(object) {
    const { classes } = this.props;
    return Object.keys(object).map((key) => {
      if (typeof object[key] == "object") {
        return <TableRow key={key}>
          <TableCell component="th" scope="row"> {key}</TableCell>
          <TableCell >
            <Table className={classes.table}>
              <TableBody>
                {this.returnAllValues(object[key])}
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

  render() {
    if (this.props.keycloak && this.props.keycloak.authenticated) {
      const { classes } = this.props;
      return (<div>
        <Typography variant="h4">Contenu du jeton</Typography>

        <Paper className={classes.root}>
          <Table className={classes.table}>
            <TableBody>
              {this.returnAllValues(this.props.keycloak.tokenParsed)}
            </TableBody>
          </Table>
        </Paper>
      </div>
      );
    }
    else return (<p>Vous êtes anonyme</p>);
  }
}


const mapStateToProps = state => ({
  keycloak: state.keycloak
});


export default connect(
  mapStateToProps,
)(withStyles(styles)(ContenuJeton));