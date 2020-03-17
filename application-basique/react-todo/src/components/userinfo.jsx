import React, { Component } from 'react';
import { connect } from "react-redux";
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import axios from 'axios';

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

class UserInfo extends Component {
    state = {
        userInfo: ''
    }

    componentDidMount() {
        var composant = this;
        axios.get('https://auth.insee.test/auth/realms/agents-insee-interne/protocol/openid-connect/userinfo').then(
            (response) => {
                composant.setState({
                    userInfo: JSON.stringify(response.data),
                })
            }).catch((error) => console.log(error))
    }

    render() {
        //  const { classes } = this.props;

        if (this.props.keycloak && this.props.keycloak.authenticated && this.props.keycloak.tokenParsed.realm_access.roles.includes("offline_access")) {
            return (
                <div>
                    <Typography variant="h4" gutterBottom>
                        User info
                    </Typography>
                    <Typography variant="body1">
                        {this.state.userInfo}
                    </Typography>
                </div>)
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
)(withStyles(styles)(UserInfo));