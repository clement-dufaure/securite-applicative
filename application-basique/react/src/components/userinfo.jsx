import Typography from '@material-ui/core/Typography';
import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import axios from '../utils/axiosToken';

const UserInfo = () => {
    const [userInfo, setUserInfo] = useState('');
    const keycloak = useSelector(state => state.keycloak);
    axios.get('http://localhost:8180/auth/realms/test/protocol/openid-connect/userinfo').then(
        (response) => {
            setUserInfo(JSON.stringify(response.data))
        }).catch((error) => console.log(error))

    if (keycloak && keycloak.authenticated && keycloak.tokenParsed.realm_access.roles.includes("offline_access")) {
        return (
            <>
                <Typography variant="h4" gutterBottom>
                    User info
                    </Typography>
                <Typography variant="body1">
                    {userInfo}
                </Typography>
            </>)
    } else {
        return (
            <p>Accès refusé</p>
        );
    }

}

export default UserInfo;