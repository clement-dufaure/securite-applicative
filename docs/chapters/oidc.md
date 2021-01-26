<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 6
### OpenID Connect







<!-- .slide: class="slide" -->
### Les grands principes
- Basé sur les protocoles OAuth 2.0
- OAuth2 ne gère que l'autorisation : il permet d'autoriser l'accès à une ressource en déléguant la source de contôle à une autre entité
- Les système d'autorisations sont basés sur un token : on doit spécifier à l'application consommatrice le moyen de le vérifier






<!-- .slide: class="slide" -->
### Les grands principes : OAuth2
- Un resource owner se connecte à une application cliente d'un resource server.
- Le resource server accepte de donner les resources appartenant au resource owner s'il lui fournit un jeton obtenu via un authorization server.









<!-- .slide: class="slide" -->
### Les grands principes
<img src="./images/oidc.png" width="70%" />








<!-- .slide: class="slide" -->
### Petite digression : Consentement
- Avec un fournisseur d'identité partagé, potentiellement sur plusieurs domaines différents, se pose la question du consentement
- Le fournisseur d'identité va transmettre un certain nombre d'information à l'application cliente via le jeton
- Le ressource owner peut contrôler les informations transmises

Exemple
- Un service lambda vous propose de vous athentifiez avec Facebook, Facebook vous avertit des informations qui vont être transmises au site tiers







<!-- .slide: class="slide" -->
### Les grands principes : OAuth2
- Access token / refresh token
- La sécurité de OAuth2 se base grandement sur HTTPS (jetons signés non chiffrés)







<!-- .slide: class="slide" -->
### Les grands principes : OAuth2
Modes d'autorisation
- Authorization Code : le serveur d'autorisation fournit un code d'autorisation, transmis au client et échangé contre le jeton auprès du serveur d'autorisation
- Implicite : le serveur d'autorisation fournit directement un jeton
- Resource Owner Password Credentials : le client gère les credentials, il demande directement le jeton au serveur d'autorisation en échange des credentials
- Client Credentials Grant : jeton d'accès propre au client et non à l'utilisateur








<!-- .slide: class="slide" -->
### Les grands principes : OAuth2
Modes d'autorisation
- Authorization Code : cas général
- Implicite : compréhensible dans des applis javascript (mais authorization code marche très bien) : deprécié
- Resource Owner Password Credentials : en dernier recours dans le cas de client lourd
- Client Credentials Grant : pour des batchs qui doivent se connecter à un web service sécurisé par Keycloak sans avoir à dédoubler l'authentification







<!-- .slide: class="slide" -->
### OpenIDConnect OAuth2
- OpenIDConnect ajoute l'authentification sur OAuth2
- Le jeton est au format JWT







<!-- .slide: class="slide" -->
### JWT
Header
```json
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "eYjLrajOiGhn_R3Yfmok2c3v3PrUZZ9ibXob0MowXvw"
}
```






<!-- .slide: class="slide" -->
### JWT
Payload
```json
{
 "exp": 1528739605,"iat": 1528739305,
 "iss": "https://mon.serveur.keycloak/auth/realms/formation",
 "aud": "client-test-web",
 "typ": "Bearer",
 "azp": "client-test-web",
 "auth_time": 1528739299,
 "session_state": "ac727f77-fcc4-403c-9765-6ee58bc980f5",
 "acr": "1",
 "allowed-origins": [
   "*"
 ],
 "realm_access": {
   "roles": ["Adminstrateur_monAppli"]
 },
 "resource_access": {"account": {"roles": ["manage-account","manage-account-links","view-profile"]}},
 "name": "Alphonse Robichu",
 "preferred_username": "idep",
 "given_name": "Alphonse",
 "family_name": "Robichu",
 "email": "alphonse.robichu@my.org"
}
```










<!-- .slide: class="slide" -->
### Les grands principes
- Jeton au format JWT - JSON Web Token
- Données au format JSON, en trois parties : header, payload et signature
- JWT = base64(header).base64(payload).base64(signature)

- Plus de détails : https://jwt.io







<!-- .slide: class="slide" -->
### Les grands principes
- Application WEB : on peut renseigner un secret à l'application pour s'assurer que c'est bien elle qui demande le jeton
- Cas du flux "Client Credential" : le secret est le "mot de passe" de l'application
- Application JS : un secret ne serait jamais secret => demandes non authentifiées






<!-- .slide: class="slide" -->
### Les grands principes
- Application WEB frontend : on s'attend à des utilisateurs non identifiés, à identifier, gestion de session
- Application WEB backend (bearer-only) : on ne s'attend qu'à des requêtes authentifiées
- Dans le cas du backend les tentatives d'authentification implicites sont bloquées







<!-- .slide: class="slide" -->
### Obtention d'un code
- Authentification sur Keycloak (Formulaire, Kerberos, autre IDP)
- Redirection vers l'appli avec un code :

```
GET https://localhost:8443/sso/login?state=9115c618-b01c-4966-a26e-8171caf30dcf&session_state=cc84a1d5-2f0e-4100-aef9-be6aa4278d91
&code=eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.[...]
```






<!-- .slide: class="slide" -->
### Appel du serveur à Keycloak
```
POST /auth/realms/test/protocol/openid-connect/token HTTP/1.1
Authorization: Basic YXBwbGktdGVzdDphOWUzMmYzMC1hNzFiLTQ0Y2ItODQwMi1iZjdkY2VmNzlhMTM= # appli-test:[Secret]
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
[...]

grant_type=authorization_code&code=eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.[...]&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fsso%2Flogin&client_session_state=CD681BDBFD032E9003B761532EE3C489&client_session_host=w100029342
```
Dans le cas d'appli "public" : pas d'entete Authorization





<!-- .slide: class="slide" -->
### Réponse de Keycloak
```json
{
  "access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  "expires_in":300,
  "refresh_expires_in":1800,
  "refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  "token_type":"bearer",
  "id_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  "not-before-policy":0,
  "session_state":"34dc14d5-bbd0-4c15-86b4-2d40629e0ced",
  "scope":""
}
```






<!-- .slide: class="slide" -->
### Demande de refresh
```
POST /auth/realms/test/protocol/openid-connect/token HTTP/1.1
Authorization: Basic YXBwbGktdGVzdDphOWUzMmYzMC1hNzFiLTQ0Y2ItODQwMi1iZjdkY2VmNzlhMTM=
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
[...]

grant_type=refresh_token&refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]
```
Dans le cas d'appli "public" : pas d'entete Authorization




<!-- .slide: class="slide" -->
### Réponse de Keycloak
```json
{
  "access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  "expires_in":300,
  "refresh_expires_in":1800,
  "refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  "token_type":"bearer",
  "id_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  "not-before-policy":0,
  "session_state":"2bf0c940-ac60-488c-bc32-d6ced922f90d",
  "scope":""
}
```






<!-- .slide: class="slide" -->
### Validation des accès d'un WS (bearer-only)
- N'a pas besoin de demander de token
- S'attend à avoir toujours un access-token valide et non expiré
- Valide ces éléments ou rejette la requête





<!-- .slide: class="slide" -->
### Validation des accès d'un WS (bearer-only)
```
GET /ma/ressource HTTP/1.1
Authorization: Bearer  eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]  
```






<!-- .slide: class="slide" -->
### Validation des accès d'un jeton
- Le serveur doit connaitre la clé publique du serveur d'authorisation (Keycloak) pour valider les access-token (c'est l'unique vérification faite dans le cas d'un bearer-only)

```
GET /auth/realms/formation/.well-known/openid-configuration HTTP/1.1
```

```json
{
  "issuer": "https://mon.serveur.keycloak/auth/realms/formation",
  "authorization_endpoint": "https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/auth",
  "token_endpoint": "https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/token",
  "end_session_endpoint": "https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/logout",
  "jwks_uri": "https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/certs",
  ...
}
```

- On peut aussi la stocker en local






<!-- .slide: class="slide" -->
### Connexion avec compte applicatif
- Récupération d'un jeton :

```
POST auth/realms/formation/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET 
```

```json
{
  "access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]",
  ...
}
```







<!-- .slide: class="slide" -->
### OpenIDConnect
- Exemple d'implémentation : Keycloak
- La grande particularité de Keycloak est de pouvoir faire toutes les configurations spécifiques à un Realm, ce qui permet d'être plus souple sur les adaptations à chaque appli
- L'administration de realm peut potentielement servir à manipuler les contacts
- Nous souhaitons pour le moment maintenir le WS gestion des contacts comme unique "modifieur" de contact
- Seule exception : le mot de passe, Keycloak propose aux utilisateurs de réinitialiser leur mot de passe (de série)








<!-- .slide: class="slide" -->
### Configuration OpenIDConnect
Entre 3 et 5 paramètres suffisent

Dans tous les cas
- L'url de keycloak
- Le realm
- Le client_id

Salon le type du client
- Le type de client : public, confidential(par defaut pour les adapters java), bearer-only
- le secret si le client est confidential









<!-- .slide: class="slide" -->
### Configuration Keycloak
Cas classique de configuration avec keycloak.json

https://www.keycloak.org/docs/latest/securing_apps/#_java_adapter_config


```json
{
  "realm" : "formation-secu-applicative",
  "resource" : "client-test-web",
  "auth-server-url" : "https://mon.serveur.keycloak/auth",
  "bearer-only" : false,
  "public-client" : false,
   "credentials" : {
      "secret" : "1a5b0b89-c23a-4d3b-9653-72faf8754a61"
   }

}
```






<!-- .slide: class="slide" -->
### Configuration Keycloak
Cas plus évolué de configuration avec keycloak.json

```json
{
  "realm" : "demo",
  "resource" : "customer-portal",
  "realm-public-key" : "MIGfMA0GCSqGSIb3D...31LwIDAQAB",
  "auth-server-url" : "https://localhost:8443/auth",
  "ssl-required" : "external",
  "use-resource-role-mappings" : false,
  "public-client" : true,  
  "bearer-only" : false,
  "enable-cors" : true,"cors-max-age" : 1000,"cors-allowed-methods" : "POST, PUT, DELETE, GET",
  "enable-basic-auth" : false,
  "expose-token" : true,
   "credentials" : {"secret" : "234234-234234-234234"},
  "always-refresh-token": false,
  "token-minimum-time-to-live" : 10,
  "public-key-cache-ttl": 86400,
  "principal-attribute" : "nom attribut jeton"
}
```







<!-- .slide: class="slide" -->
### Configuration Keycloak
Cas plus évolué de configuration avec keycloak.json
- realm-public-key : spécifier "en dur" la clé publique du serveur
- public-client : true en l'absence de credentials (déconseillé pour les appmis web)
- bearer-only : true si web service 
- enable-cors, cors-max-age, cors-allowed-methods : validation des requetes CORS selon configuration keycloak. Les origines autorisées spécifiées dans l'administartion de keycloak sont renseignées dans le token.
- expose-token : définition d'une url pour afficher le jeton de la session (debug)
- always-refresh-token : pour forcer le jeton à se rafraichir systématiquement à chaque passage sur le filtre
- token-minimum-time-to-live : temps minimum d'activité du jeton avant rafraichissement par le filtre
- public-key-cache-ttl : durée de rafraichissement de la clé publique du serveur
- principal-attribute : nom attribut du jeton à renseigner pour request.getRemoteUser() (par defaut sub = référence illisible keycloak)








