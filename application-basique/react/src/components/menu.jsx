import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import { Link } from "react-router-dom";
import Divider from '@material-ui/core/Divider';
import { Typography } from '@material-ui/core';

const styles = theme => ({
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
  dividerFullWidth: {
    margin: `5px 0 0 ${theme.spacing.unit * 2}px`,
  },
});

class SimpleMenu extends React.Component {
  state = {
    anchorEl: null,
  };

  handleClick = event => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  render() {
    const { classes } = this.props;
    const { anchorEl } = this.state;

    return (
      <div>
        <IconButton
          className={classes.menuButton}
          color="inherit" aria-label="Menu"
          aria-owns={anchorEl ? 'simple-menu' : undefined}
          aria-haspopup="true"
          onClick={this.handleClick}
        >
          <MenuIcon />
        </IconButton>
        <Menu
          id="simple-menu"
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={this.handleClose}
        >
          <Link to="/tokenParsed" style={{ textDecoration: 'none', display: 'block' }}>
            <MenuItem onClick={this.handleClose} >Contenu du jeton</MenuItem>
          </Link>
          <Link to="/token" style={{ textDecoration: 'none', display: 'block' }}>
            <MenuItem onClick={this.handleClose} >Récupération d'un AccessToken</MenuItem>
          </Link>

          <Divider component="li" />
          <li><Typography className={classes.dividerFullWidth} color="textSecondary" variant="caption">Fonctions réservées</Typography></li>

          <Link to="/scope" style={{ textDecoration: 'none', display: 'block' }}>
            <MenuItem onClick={this.handleClose} >Récupération de user-info</MenuItem>
          </Link>
        </Menu>
      </div>
    );
  }
}

export default withStyles(styles)(SimpleMenu);