import axios from 'axios';
import store from '../redux/store'

axios.interceptors.request.use(
    async (config) => {
        // On attend une mise à jour du token si nécessaire
        await new Promise((resolve, reject) => {
            store.getState().keycloak.updateToken(30).success(() => {
                resolve();
            }).error(() => reject());
        });
        config.headers.Authorization = 'Bearer ' + store.getState().keycloak.token;
        return config;
    }
)

export default axios;
