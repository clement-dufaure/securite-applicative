import axios from 'axios';
import store from '../redux/store'

axios.interceptors.request.use(
    async (config) => {
        // mettre a jour le token si besoin et l'ajouter à la requete
        return config;
    }
)

export default axios;
