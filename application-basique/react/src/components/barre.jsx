import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Login from './login.jsx';
import MyMenu from './menu.jsx';
import { connect } from "react-redux";

const styles = {
  root: {
    flexGrow: 1,
  },
  grow: {
    flexGrow: 1,
  },
  menuButton: {
    marginLeft: -12,
    marginRight: 20,
  },
};

class ButtonAppBar extends React.Component {
  render() {
    var messageConnecte = "";
    if (this.props.keycloak && this.props.keycloak.authenticated) {
      messageConnecte = "Connecté en tant que " + this.props.keycloak.tokenParsed.preferred_username + " (" + this.props.keycloak.realm + ")";
    }
    const { classes } = this.props;
    return (
      <div className={classes.root}>
        <AppBar position="static">
          <Toolbar>
            <MyMenu />
            <Typography variant="h6" color="inherit" className={classes.grow}>
              Démonstration Keycloak avec React
            </Typography>
            <Typography variant="h6" color="inherit" className={classes.grow}>
              {messageConnecte}
            </Typography>
            <Login />
          </Toolbar>
        </AppBar>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  keycloak: state.keycloak
});

export default connect(
  mapStateToProps,
)(withStyles(styles)(ButtonAppBar));