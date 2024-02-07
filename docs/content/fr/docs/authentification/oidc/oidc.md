---
title : "Fonctionnement d'OpenID Connect"
description: ""
lead: ""
draft: false
images: []
weight: 610
menu:
  docs:
    parent: "oidc"
mermaid: true
---


Protocole grandement basé sur le protocole OAuth 2.0.

OAuth2 ne gère que l'autorisation : il permet d'autoriser l'accès à une ressource en déléguant la source de contôle à une autre entité

Les système d'autorisations sont basés sur un token : on doit spécifier à l'application consommatrice le moyen de le vérifier

**Un peu de vocabulaire**
- Un *resource owner* se connecte à une application cliente d'un *resource server*.
- Le *resource server* accepte de donner les resources appartenant au *resource owner* s'il lui fournit un jeton obtenu via un *authorization server*.

On distinguera le *client* qui correspond à un application ayant besoin d'une identité pour fonctionner de l'*utilisateur* qui est la personne physique qui possède une identité

## Les grands principes

### Séquencement

Avertissement : schema simplifié par rapport à la spec complète

Notre utilisateur Alphone Robichu doit se connecter à l'application de gestion. Il doit se connecter en tant qu'administrateur sur l'application de gestion. L'application de gestion fait appel à un web service du service de gestion des stocks. Cet appel doit se faire avec sa véritable identité "Alphonse Robichu" et non avec un compte générique.


{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as AppGestion
    participant S as AppStock 
    box LightGrey Authorization Server
    participant E as Authentication Endpoint
    participant F as Token Endpoint
    end
    U->>A: Hello !
    A->>U: 302 Authentifie toi ! 
    U->>E: AppGestion me demande me m'authentifier chez toi
    U->>E: Login (id/mdp par exemple)
    U-->>E: Consentement
    E->>U: 302 Donne ce code à AppGestion
    U->>A: Voici le code fourni par l'IDP
    A->>F: Je suis appGestion, puis avoir un jeton contre ce code ?
    F->>A: {accessToken, refreshToken}
    A->>A: check {accessToken}
    loop
        A->>S: get /resource/stocks muni de {accessToken}
        S->>S: check {accessToken}
        S->>A: /ressource/stocks
    end
    Note over A,S: {accessToken} expiré
    A->>F: Je suis appGestion, puis avoir un nouveau jeton contre ce {refreshToken} ?
{{< /mermaid >}}



### Le consentement
- Avec un fournisseur d'identité partagé, potentiellement sur plusieurs domaines différents, se pose la question du consentement
- Le fournisseur d'identité va transmettre un certain nombre d'information à l'application cliente via le jeton
- Le ressource owner peut contrôler les informations transmises

Exemple
- Un service lambda vous propose de vous athentifiez avec Facebook, Facebook vous avertit des informations qui vont être transmises au site tiers


### Les jetons
- Access token : marqueur de l'identité. On doit être capable via son contenu signé et/ou via requete au serveur d'autorisation d'associer de façon fiable une identité à un access token
- Refresh token : NE doit servir QU'**une seule fois** pour obtenir un nouveau couple {access token, refresh token}
- Il existe aussi l'ID Token initialement dédié pour porter les information d'identité. La spec OAuth ne précisant rien sur le format des access token. Dans la pratique on peut rendre égaux les Access et ID Token

**Confidentialité des jeton**
Les information stockées dans le jeton NE sont pas CHIFFRÉES. Dans un mode qui utilise massivement https, la sécurité de OAuth2 se base grandement sur HTTPS.
On considère que le jetons non chiffré tansite toujours sur un canal chiffrés.

**Intégrité du jeton**
Même sur un canal chiffré, n'importe qui pourrait forger un access token ressemblant à un access token au nom de Alphonse Robichu mais non issu du serveur d'autorisation.

L'unique méthode de controle présenté ici sera **la signature**.


## Les différents flows

### Authorization code

C'est le mode de fonctionnement Standard présenté dans la partie précédente

1/ login => code
2/ code => token

**Usage ?**
Systématique à moins de rentrer dans les cas suivants !

En particulier dans le cadre d'application de type "exécution serveur" au sein d'un même SI, il permet de limiter grandement la circulation des access token sur le réseau.

### Implicite : **DÉPRÉCIÉ** !!!

Ressemble fortement au précédent mais en simplifié

1/ login => token

Clairement, le serveur fournit directement un token dans la redirection et non un code à échanger.
Ce flow était historiquement utilisé dans le cadre des applications de type javascript éxécutés dans le navigateur acr le code semblait alourdir le flow sans apporter de gains de sécurité.

La position a été depuis revu pour vaforiser le flow Authorization code dans ce contexte également

### Resource Owner Password Credentials ou Direct Grant


{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as AppGestion
    participant S as AppStock 
    box LightGrey Authorization Server
    participant F as Token Endpoint
    end
    U->>A: Hello !
    A->>U: Ton login et mot de passe ? 
    U->>A: Login id/mdp
    A->>F: Je suis appGestion, puis avoir un jeton contre ces id/mdp ?
    F->>A: {accessToken, refreshToken}
    A->>A: check {accessToken}
    loop
        A->>S: get /resource/stocks muni de {accessToken}
        S->>S: check {accessToken}
        S->>A: /ressource/stocks
    end
    Note over A,S: {accessToken} expiré
    A->>F: Je suis appGestion, puis avoir un nouveau jeton contre ce {refreshToken} ?
{{< /mermaid >}}


**Usage ?**

Ce flow est à réserver aux applications qui ne peuvent pas gérer des redirections, typiquement des applications "lourdes" installées sur un poste.
A noter que la grande différence avec le flow Standard est que l'application cliente connait le mot de passe de l'utilisateur, ce que l'on cherche plutôt à éviter avec l'utilisation d'un fournisseur d'identité.

### Client Credentials Grant

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    participant A as AppGestion
    participant S as AppStock 
    box LightGrey Authorization Server
    participant F as Token Endpoint
    end
    A->>F: Je suis appGestion, puis avoir un jeton en tant que appGestion ?
    F->>A: {accessToken, refreshToken}
    A->>A: check {accessToken}
    loop
        A->>S: get /resource/stocks muni de {accessToken}
        S->>S: check {accessToken}
        S->>A: /ressource/stocks
    end
    A->>F: Je suis appGestion, puis avoir un nouveau jeton contre ce {refreshToken} ?
{{< /mermaid >}}

Le jeton d'accès propre au client et non à un utilisateur

**Usage ?**

Soit dans les cas où l'on a pas d'utilisateur du tout comme un batch. Soit dans les cas où on considère que l'accès au web service "backend" n'a pas de sens à être identifié en tant qu'utilisateur.


## Le jeton JWT

Les jetons échangés seront sous un format dit JWT, JSON Web Token.

Les données au format JSON, en trois parties : header, payload et signature

### Header
```json
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "eYjLrajOiGhn_R3Yfmok2c3v3PrUZZ9ibXob0MowXvw"
}
```

Présente notamment l'algorithme de signature utilisé, dans l'exemple ici, le jeton est signé avec une signature asymétrique. Le serveur d'authentification possède une clé privée secrète avec laquelle il a signée le jeton.

Il diffuse ouvertement sa clé publique qui permet de controller la signature.

### Payload
```json
{
 "exp": 1528739605,
 "iat": 1528739305,
 "iss": "https://mon.serveur.keycloak/auth/realms/formation",
 "aud": "client-test-web",
 "typ": "Bearer",
 "azp": "client-test-web",
 "auth_time": 1528739299,
 "session_state": "ac727f77-fcc4-403c-9765-6ee58bc980f5",
 "acr": "1",
 "roles": ["Responsable_Clients","Gestionnaire_Stocks"],
 "name": "Alphonse Robichu",
 "preferred_username": "id",
 "given_name": "Alphonse",
 "family_name": "Robichu",
 "email": "alphonse.robichu@my.org"
}
```

Les infos "utiles" transités par le jeton dont certaines dédiées aux contrôle ou à des précisions sur le mode d'authentification telles que
    - iat : le serveur qui a émis le jeton
    - exp : la date d'expiration du jeton
    - acr : le niveau de fiabilité de l'authentification (nombre de facteurs par exemple)

On appelle **claim** chaque élément de ce jeton.

Les fournisseur d'identité fournissent généralement un ensemble de claim par défaut mais le client est censé préciser les claims requi via un paramètre de *scope* à convenir entre client et serveur d'autorisation.



## "Je suis appGestion" : comment vérifier ?

### Par identifiant/mot de passe ?

On va parler de *client_id* et de *client_secret*

Efficace dans le cas ou AppGestion est une application purement serveur (pas une appli javascript tournant dans un navigateur) ou dans les cas Direct Grant et Client Credential. 

Dans le cas de Client Credential, c'est d'ailleurs l'unique protection de récupération du jeton.

Cette vérification est totalement inéfficace dans le cas d'une application de type javascript tournant sur navigateur, en effet dela reviendrait à écrire le mot de passe dans le code source recu par l'utilisateur...

Dans ce dernier cas, les requêtes sur `/token` seront donc non authentifiées.

Si une application cliente possède un mot de passe on parlera de *client confidential*, sinon c'est un *client public*

### Par url de redirection ?

Quand le cas du flow Standard, la redirection va forcément faire mention à l'url de retour vers l'application. On peut donc contrôler le format de cette url et empecher de renvoyer vers une url qui n'est pas censée être utilisée en fonctionnement normal.

Cette protection n'a aucune signification dans le cas de Direct Grant ou de Client Credential.

### Par url d'origine ? 

Dans le cas des application de type javascript éxécutées dans navigateur, les requetes `/token` seront éxécutées depuis les script et un navigateur effectuera ainsi la protection CORS.


## Cas du web service "backend"

Si un service n'est pas une ihm, il n'est pas censé demander une identité mais uniquement les contrôler.

On differencie explicitement les application comme AppGestion dans l'exemple :
- ont une ihm
- les utilisateurs s'y connecte en n'téant pas encore authentifié
- l'application doit être en mesure de rediriger vers l'idp, donc de "créer" un identité
- l'application gère des sessions de façon à ne pas réauthentifier l'utilisateur à chaque requête

de l'application web service de gestion des stocks qui :
- n'a pas d'ihm
- n'est censé recevoir que des requêtes identifiées "explicitement"
- **n'est pas censé réagir à une requete anonyme autrement que par 401**
- l'identité est revérifiée à chaque requête (pas de gestion de session)

On parlera pour ce dernier type d'application, d'application *bearer-only*, c'est à dire dont le seul mode d'authentification doit être la présence d'un jeton dans la requête.

Ces applications n'ont en général pas à être connu du fournisseur d'identité

### Et le swagger ?

Le swagger n'est **PAS** l'api, c'est techniquement une seconde application exposée sur le même serveur que l'api qui doit être traitée comme une application à part entière.

En l'occurence il s'agit d'une application de type javascript éxécutée sur navigateur.



## Les requêtes à la loupe

### Les mécanismes de sécurité

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as AppGestion
    participant S as AppStock 
    participant E as Authentication Server <br/> (/auth et /token)
    U->>A: Hello !
    note over A: Génération d'un state, d'un nonce et d'un (code_verifier,code_challenge) aléatoire
    A->>U: 302 avec redirect_uri + state + nonce + code_challenge
    U->>E: authentifie moi et retour vers redirect_uri 
    note over E: sauvegarde du nonce et code_challenge en session
    note over E: genération aléatoire d'un session_state
    E->>U: 302 code + state (passe plat) + session_state + aud
    U->>A: code + state (passe plat) + session_state + aud
    note over A: contrôle du state
    A->>E: code + code_verifier
    note over E: vérification adéquation (code_verifier,code_challenge)
    E->>A: {accessToken, refreshToken, session_state}
    note over A: controle du session_state
    A->>A: check {accessToken}
    note over A: lecture et contrôle du claim nonce de l'accessToken <br/> (en plus de la signature)
    note over A: controle du claim aud
    A->>S: get /resource/stocks muni de {accessToken}
    note over S: controle du claim aud
{{< /mermaid >}}


Le state ?
- Éviter les attaque CSRF par un controle du déroulé du processus

session_state et nonce ?
- Éviter le rejeu, il permet d'éviter de renvoyer un jeton intercepté encore valable vers d'autre service. On parle de contrôle de corrélation entre requêtes et réponse d'authentification 

Les éléments nonce, session_state sont maintenu dans la session de l'utilisateur côté fournisseur d'identité. La session de l'utilisateur coté fournisseur d'identité (idle et max) doit donc être cohérente avec la durée de rafraichissement possible du jeton (penser aux cas de formulaire long à remplir).

(code_verifier,code_challenge) ?
- Mécanisme PKCE (Proof Key for Code Exchange) : protection CSRF

aud ? ou Audience
- paramétré par le fournisseur d'identité par client, il permet de préciser la portée d'usage du jeton. L'audience peut être vérifié sur un web service transitif.
Ce claim permet de distinguer des zones d'usage des tokens.


### Flow Authorization Code

Connexion à Appgestion, redirection vers l'*authorization server*.

```http
GET https://mon.fournisseur.identite/auth
?redirect_uri=https://mon.appli.gestion/sso/callback
&state=9115c618-b01c-4966-a26e-8171caf30dcf
&.......
```

- Le paramètre redirect_uri permet le contrôle de l'url de l'application de gestion où sera envoyé le code
- Le paramètre state est généré aléatoirement par le client, il permet d'éviter les attaque de type CSRF, il devra être renvoyé et contrôlé par le client au retour : l'application le stocke associé à la session de l'utilisateur


Suite à l'authentification sur l'*authorization server*, redirection vers l'application

```http
GET https://mon.appli.gestion/sso/callback
?state=9115c618-b01c-4966-a26e-8171caf30dcf
&session_state=cc84a1d5-2f0e-4100-aef9-be6aa4278d91
&code=eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.[...]
&.......
```

- Le state permet donc de s'assurer que l'application est bien initiatrice du processus d'authentification
- Le session_state est généré aléatoirement par le fournisseur d'identité, dans la même logique que le state, il permettra de corréler la demande d'authentification au reste du processus (notamment pour éviter les rejeu)
- Le code à échanger contre un token. **A noter que durant sa durée de validité, le code est autant critique que le mot de passe de l'utilisateur**


L'application demande un jeton

```http
POST https://mon.fournisseur.identite/token/token
Authorization: Basic YXBwbGktdGVzdDphOWUzMmYzMC1hNzFiLTQ0Y2ItODQwMi1iZjdkY2VmNzlhMTM= # appli-test:[Secret]
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
[...]

grant_type=authorization_code
&code=eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.[...]
```

- grant_type : le type de troc demandé au serveur, ici on va échanger un code contre un token
- la code
- l'authentification du client le cas échéant en authentification Basic


Dans le cas de client *public* (javascript), il n'y a donc pas d'entete Authorization

Si tout se pass bien, le serveur d'autorisation va donc répondre :

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

On retrouve notamment le session_state permettant de controller la corrélation des demandes.

On obtient donc les différents token attendus :
- L'access token qui peut égaler l'ID token dans son contenu "utile" mais qui est censé être le seul à pouvoir donner authentification via échange de jeton. **A noter que durant sa durée de validité, l'access token est autant critique que le mot de passe de l'utilisateur**. Comme il peut être ammené à beaucoup circuler sur le réseau, sa durée de validité est censé être très réduite (recommandé vers 5 minutes).
- le refresh token est censé être à usage unique, pour renouveler la demande précédente mais en épargnant l'utilisateur d'aller rechercher un code.

```http
POST https://mon.fournisseur.identite/token/token
Authorization: Basic YXBwbGktdGVzdDphOWUzMmYzMC1hNzFiLTQ0Y2ItODQwMi1iZjdkY2VmNzlhMTM=
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
[...]

grant_type=refresh_token&refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]
```
(Et toujours dans le cas de *client public* : pas d'entete Authorization)

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

A noter que l'on obtient un **nouveau** refresh token qui est censé invalider le précédent

Un refresh token ne circule donc en étant valide que deux fois sur le réseau, d'où la fait que sa durée peut être longue (chiffrée en heures).



### Contrôle du jeton

Le serveur doit connaitre la clé publique du serveur d'autorisation pour valider les access-token (c'est d'ailleurs l'unique vérification faite dans le cas d'un bearer-only)

La clé publique de validation de la signature peut être stockée en dûr dans l'application cliente.

On peut aussi la demander au serveur d'autorisation.

```http
GET https://mon.foutrnisseur.identite/.well-known/openid-configuration
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


### Connexion à un service *bearer-only*

```http
GET https://ma.gestion.de.stocks/stocks/ressource 
Authorization: Bearer  eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMWs3ZXNUbG1XTHBVSXIydEp2c0E4V0tDS1EtNWNvUjVNVmQwVFRHRlFBIn0.[...]  
```

L'acces token est envoyé brut dans l'entête Authorization


### Flow Client Credentials
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


## Configuration OpenIDConnect

L'appli cliente doit connaitre quelques endpoints du serveur d'autorisation
- endpoint auth
- endpoint token
- enpoint jwks (clés publiques)

ces endpoints sont de façon standard donnés sur l'url https://mon.foutrnisseur.identite/.well-known/openid-configuration

Une configuration doit être négociée entre l'application client et le serveur d'autorisation
- Le *client_id*
- Le *client_secret* le cas échéant (donc pas pour les applis javascript)


Une application de bearer-only n'a besoin que de la clé publique de validation ou de l'url pour l'obtenir.

Cette url est accessible sans authentification, il n'est pas nécessaire de configurer le fournisseur d'dentité autour de l'identité du client *bearer-only* 

*Le concept d'expiration rapide de l'access token et de renouvellement entraine génralement plusieurs configuration requisse sur la politique de renouvellement des jeton*
- A chaque requête ?
- Après expiration ?
- Toutes les n secondes fixes ?
- n secondes avant expiration ?


*Des configurations plus applicatives sont souvent demandées telles que le claim fournissant le nom d'utilisateur ou la liste de ses habilitations*







