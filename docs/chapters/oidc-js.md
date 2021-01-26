<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 6.2
### OpenID Connect et JavaScript








<!-- .slide: class="slide" -->
### Keycloak et javascript
Fonctionnement (adapter js Keycloak)
- Keycloak s'initialise (préparation des méthodes) et attend un login
- Au login Keycloak place dans le localStorage (ou Cookie en cas d'échec) le state
- L'utilisateur est redirigé vers Keycloak, il s'authentifie et il est redirigé vers l'application initiale avec en paramètre un code.
- En revenant vers l'application avec les paramètres code et state, la phase d'initialisation de Keycloak comprend que l'authentification a été faite, il controle le state
- L'adapter crée un iframe, qui se connecte à Keycloak avec le code pour obtenir les token (access,refresh,id)







<!-- .slide: class="slide" -->
### Keycloak et javascript
<img src="./images/Keycloak JS.png" width="60%" />







<!-- .slide: class="slide" -->
### Keycloak et javascript : Sécurité ?
- Rappel problématique : personne en dehors de l'appli et de l'utilisateur ne doit avoir d'info sur le jeton
- Appli Web : aucun problème, l'utilisateur n'a jamais connaissance du jeton, il ne connait qu'un code à usage unique, l'échange de jeton se fait entre le serveur et Keycloak via un secret.
- Appli Javascript : appli "locale", l'utilisateur manipule directement son jeton en local. Pas de possibilité de secret, car le code est local. Un pirate peut donc créer son appli javascript ressemblant à la notre, en incluant une brique keycloak et l'utilisateur fournirait ainsi son jeton au pirate. Le jeton permet ensuite de se connecter à d'autres services.
- Le seul moyen de sécuriser est donc pour Keycloak de s'assurer que l'appli demandant une authentification a bien le droit, et pour cela seule l'url de l'application demandant autorisation peut permettre un filtre









<!-- .slide: class="slide" -->
### Keycloak et javascript : Sécurité ?
Deux fonctionnements
- Par redirection : on peut spécifier les urls de redirection autorisées par Keycloak
- Par CORS : on spécifie les domaines sources qui peuvent demander une authentification









<!-- .slide: class="slide" -->
### Keycloak et javascript
- Une possibilité simple : utiliser l'adapter keycloak directement disponible sur le serveur Keycloak en https://mon.serveur.keycloak/auth/js/keycloak.js
- Met à disposition un objet Keycloak, documentation sur https://www.keycloak.org/docs/latest/securing_apps/#_javascript_adapter
```javascript
var keycloak = Keycloak(); // recherche un keycloak.json dans le dossier courant
keycloak.init(); // prépare l'objet
keycloak.login();
keycloak.logout();
```








<!-- .slide: class="slide" -->
### Keycloak et javascript
- Configuration via keycloak.json, par défaut détecter dans le même dossier que la page
```json
{
	"realm": "formation-secu-applicative",
	"resource": "client-test-js",
	"auth-server-url": "https://mon.serveur.keycloak/auth"
}
```
- ou Keycloak('chemin vers fichier json')
- Configuration sans fichier annexe
```javascript
var keycloak = Keycloak({
	url : 'https://mon.serveur.keycloak/auth',
	realm : 'formation',
	clientId : 'client-test-js'
});
```







<!-- .slide: class="slide" -->
### Keycloak et javascript
- Pour rendre le login automatique sur une page
```javascript
keycloak.init({
		onLoad : 'login-required'
	});
```

- Pour vérifier si l'utilisateur est connecté sans l'authentifier
```javascript
  keycloak.init({
  		onLoad : 'check-sso'
  	});
```








<!-- .slide: class="slide" -->
### Keycloak et javascript
- Effectuer des opération après authentification
```javascript
keycloak.init({
		onLoad : 'login-required'
	}).success(function(){
    //L'utilisateur est forcément connecté ici
  })
```
```javascript
  keycloak.init().success(function(authenticated){
    //authenticated = true si l'utilisateur est connecté
  });
```
Code d'un bouton "Se connecter"
```
  keycloak.login()
```





<!-- .slide: class="slide" -->
### Keycloak et javascript
- Rafraichir le jeton
```javascript
keycloak.updateToken();
```
- Rafraichir le jeton si nécessaire, c'est à dire uniquement s'il expire dans les X secondes
```javascript
keycloak.updateToken(X);
```





<!-- .slide: class="slide" -->
### Keycloak et javascript
- Appel au backend/ à un web service
- On rafraichit le jeton s'il expire "bientôt" pour ne pas avoir de surprises si le traitement dure un peu
```javascript
keycloak.updateToken(30).success(function() {
  var url = 'https://localhost:8443/ws/ressource';
  var req = new XMLHttpRequest();
  req.open('GET', url, true);
  req.setRequestHeader('Accept', 'application/json');
  req.setRequestHeader('Authorization', 'Bearer ' + keycloak.token);

  req.onreadystatechange = function() {
    if (req.readyState == 4) {
    if (req.status == 200) {
      $("#userinfo").text(req.responseText);
    } else if (req.status == 403) {
      alert('Forbidden');
    }
  }
}
req.send();
};
		})
```








<!-- .slide: class="slide" -->
### Keycloak et javascript
- Logout avec la fonction keycloak.logout()
- Peut prendre en argument le lien de redirection après logout côté keycloak
```javascript
keycloak.logout({
			redirectUri : 'https://localhost:8443'
		});
```
- On ne parle que de logout côté serveur Keycloak, il n'y a par défaut aucune notion de session côté client. 
- On peut éventuellement gérer une notion de session via localStorage/localSession mais c'est alors au script de le gérer de son côté.









<!-- .slide: class="slide" -->
### Et avec React ?
Les scripts keycloak peuvent être récupérés comme dépendance :
```
npm install keycloak-js
```






<!-- .slide: class="slide" -->
### Et avec React ?
```js
import Keycloak from 'keycloak-js';

var keycloak = Keycloak(
       {
         url: 'https://mon.serveur.keycloak/auth',
         realm: 'formation',
         clientId: 'localhost-frontend'
       });
```
Avec this.props.init une action qui ajoute l'objet keycloak dans le store





<!-- .slide: class="slide" -->
### Interacton entre composant React et Keycloak
NE PAS UTILISER LE LOCALSTORAGE POUR GERER LE TOKEN
- [Avis de Auth0](https://auth0.com/docs/security/store-tokens#if-a-backend-is-present)
- Problème de sécurité : localstorage un peu trop accessible, d'une façon générale on ne stocke pas un objet si on a la possibilité de le laisser en mémoire
- L'objet sérialisé demande des bidouilles pour être regénéré correctement
- React est SPA : utiliser au maximum redux pour ne pas avoir à utiliser le localStorage ou sessionStorage

En pratique :
- Un composant de login qui gère la création, initialisation, connexion de Keycloak, et le stocke dans le store
- Les autres composants pevent se servir dans le store s'ils demandent authentification 






<!-- .slide: class="slide" -->
### Et avec React ?
```js
  componentDidMount() {
    if (!this.state.keycloakInitiated) {
      var keycloak = Keycloak();

      //Pour un login automatique
      // keycloak.init({onLoad: 'login-required'}).success(() => {this.props.init(keycloak); });
      keycloak.init().success(() => {
        this.props.init(keycloak);
      }
      );
    }
  }

```
Avec this.props.init une action qui ajoute l'objet keycloak dans le store







<!-- .slide: class="slide" -->
### Interaction entre composant React et Keycloak
Gestion de l'expiration du token
- Ajout d'un interceptor dans axios
```js
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
```






<!-- .slide: class="slide" -->
### Réduire la dépendance à Keycloak
- En utilisant des librairies plus standard oidc
https://github.com/AxaGuilDEv/react-oidc

