import React, { Component } from 'react';
import { connect } from "react-redux";
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
//import axios from 'axios'

const styles = theme => ({
    root: {
        width: '100%',
        maxWidth: 500,
    },
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
    },
    dense: {
        marginTop: 16,
    },
    menu: {
        width: 200,
    },
});


function copyToken() {
    var copyText = document.getElementById("token");
    copyText.select();
    document.execCommand("copy");
}


class HabilitationClient extends Component {
    state = {
        timeout: ""
    }

    handleChange = name => event => {
        this.setState({
            [name]: event.target.value,
        });
    };

    validateTokenAndRunFuntion = function (fonctionAExecuter) {
        this.props.mettreAJourTokenEtLancer(fonctionAExecuter);
        // keycloak.updateToken(30).success(fonctionAExecuter(keycloak)).error(function () {
        //     alert('Failed to refresh token');
        // });
    }

    render() {
        const { classes } = this.props;

        var d = new Date(0);
        d.setUTCSeconds(this.props.keycloak.tokenParsed.exp);
        console.log(d);

        if (this.props.keycloak && this.props.keycloak.authenticated) {
            return (
                <div>
                    <Typography variant="h4" gutterBottom>
                        Obtenir un jeton...
                    </Typography>
                    <Typography variant="h5" gutterBottom>
                        Jeton valide sur {this.props.keycloak.authServerUrl} sur le realm {this.props.keycloak.realm}
                    </Typography>
                    <Typography variant="h5" gutterBottom>
                        Jeton valide jusqu'à : {d.toString()}
                    </Typography>
                    <form className={classes.container} noValidate autoComplete="off" onSubmit={e => { e.preventDefault(); }} >
                        <TextField
                            id="token"
                            label="Access Token"
                            fullWidth
                            multiline
                            rowsMax="30"
                            className={classes.textField}
                            value={this.props.keycloak.token}
                            onChange={this.handleChange('nomApplication')}
                            margin="normal"
                            variant="outlined"
                        />
                    </form>
                    <Button variant="contained" color="primary"
                        onClick={() => this.validateTokenAndRunFuntion(copyToken)}>
                        Copier le token</Button>
                </div >)
        } else {
            return (
                <p>Accès refusé</p>
            );
        }

    }

}

const mapStateToProps = state => ({
    keycloak: state.keycloak,
    mettreAJourTokenEtLancer: state.mettreAJourTokenEtLancer
});

export default connect(
    mapStateToProps,
)(withStyles(styles)(HabilitationClient));