import axios from 'axios';
import store from '../redux/store';

axios.interceptors.request.use(
    async (config) => {
        // mettre a jour le token si besoin et l'ajouter à la requete
        // On attend une mise à jour du token si nécessaire
        await new Promise((resolve, reject) => {
            store.getState().keycloak.updateToken(30).then(() => {
                resolve();
            }).catch(() => reject());
        });
        config.headers.Authorization = 'Bearer ' + store.getState().keycloak.token;
        return config;
    }
)

export default axios;
