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
- Implicite : compréhensible dans des applis javascript (mais authorization code marche très bien)
- Resource Owner Password Credentials : en dernier recours dans le cas de client lourd
- Client Credentials Grant : pour des batchs qui doivent se connecter à un web service sécurisé par Keycloak sans avoir à dédoubler 
l'authentification







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
 "iss": "https://auth.insee.test/auth/realms/formation-secu-applicative",
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
   "roles": ["Utilisateurs_FormationFede"]
 },
 "resource_access": {"account": {"roles": ["manage-account","manage-account-links","view-profile"]}},
 "name": "Clément Dufaure",
 "preferred_username": "frz6ir",
 "given_name": "Clément",
 "family_name": "Dufaure",
 "email": "clement.dufaure@insee.fr"
}
```










<!-- .slide: class="slide" -->
### Les grands principes
- Jeton au format JWT - JSON Web Token
- Données au format JSON, en trois parties : header, payload et signature
- JWT = base64(header).base64(payload).base64(signature)

Exemple :
- https://outils-transverses.pages.innovation.insee.eu/documentation/







<!-- .slide: class="slide" -->
### Les grands principes
- Application WEB : on peut renseigner un secret à l'application pour s'assurer que c'est bien elle qui demande le jeton
- Cas du flux "Client Credential" : le secret est le "mot de passe" de l'application
- Application JS : un secret ne serait jamais secret => demandes non authentifiées






<!-- .slide: class="slide" -->
### Les grands principes
- Application WEB frontend : on s'attend à des utilisateurs non identifiés, à identifier, gestion de session
- Application WEB backend (bearer-only) : on ne s'attend qu'à des requêtes authentifiées
- Dans le cas du backendles tentatives d'authentification implicites sont bloquées







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
GET /auth/realms/formation-secu-applicative/ HTTP/1.1
```

```json
{
  "realm": "formation-secu-applicative",
  "public_key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAybcHUqc0emm8dAea1fHLCDR+2qq/ZGrWjd2nnRvcuK2Bpj/WVrEJVNkFRaXzHo8JGGeE3QFiceF0ZrtQ/NR9ZVNeSU80Q87MEiJHO8v0n/8gKzJK4z8BcWqhjx0SxYt2LwS+gTSQ6VaNGXL4PhAJKRI97ZXzOk4+OKheSJh7X8HKf6g7glJnnxBERbSm53tos2y2b5/+lTr9RkWiZWbK5zkELTCdsXasBTfkMtHTy1ONYQouO538hULxyoDriRF/Mbt9SibCjjP28iRBnoLPnBrhzsWOq7ErAxvzK0MJ78p82suBbDLdtdcCJFOt+UvY5mG+VOOqfz6KrUQ48fp7qQIDAQAB",
  "token-service": "https://auth.insee.test/auth/realms/formation-secu-applicative/protocol/openid-connect",
  "account-service": "https://auth.insee.test/auth/realms/formation-secu-applicative/account",
  "tokens-not-before": 0
}
```

- On peut aussi la stocker en local






<!-- .slide: class="slide" -->
### Connexion avec compte applicatif
- Récupération d'un jeton :

```
POST auth/realms/formation-secu-applicative/protocol/openid-connect/token
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
  "auth-server-url" : "https://auth.insee.test/auth",
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










<!-- .slide: class="slide" -->
### Adapter Keycloak
- Les adapters Java

https://www.keycloak.org/docs/latest/securing_apps/#java-adapters

- Adapters liés à une plateforme fortement déconseillés, car ils créent des dépendances "Hors Maven" : rappelez vous oiosaml...








<!-- .slide: class="slide" -->
### Mise en place de OpenIDConnect dans une application
- Avec le filter keycloak
- Avec l'adapter Keycloak Spring security
- En Javascript







<!-- .slide: class="slide" -->
### OIDC et filtre Jakarta EE







<!-- .slide: class="slide" -->
### Filtre Keycloak
Dépendances :
```xml
<dependency>
			<groupId>org.keycloak</groupId>
			<artifactId>keycloak-servlet-filter-adapter</artifactId>
			<version>7.0.0</version>
</dependency>
```







<!-- .slide: class="slide" -->
### Filtre Keycloak
Ajout du filtre dans web.xml
```xml
<filter>
  <filter-name>Keycloak Filter</filter-name>
  <filter-class>org.keycloak.adapters.servlet.KeycloakOIDCFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>Keycloak Filter</filter-name>
  <url-pattern>/private/*</url-pattern>
</filter-mapping>
```






<!-- .slide: class="slide" -->
### Filtre Keycloak
- Configuration par keycloak.json, dans le dossier WEB-INF
- Ou peut être placé ailleurs et défini dans un paramètre du filtre 







<!-- .slide: class="slide" -->
### Filtre Keycloak
Déclaration des urls concernés par l'authentification
```xml
<filter-mapping>
        <filter-name>Keycloak Filter</filter-name>
        <url-pattern>/private/*</url-pattern>
</filter-mapping>
```
Avec exclusion :
```xml
<init-param>
    <param-name>keycloak.config.skipPattern</param-name>
    <param-value>^/(path1|path2|path3).*</param-value>
</init-param>
```






<!-- .slide: class="slide" -->
### Filtre Keycloak
- Sur les urls correspondantes, l'authentification est assurée
- Si le filtre laisse passer la requête, l'utilisateur est authentifié sur le realm demandé en configuration
- Pour gérer les droits, il faut le gérer dans le code en lisant le jeton fourni par le filtre Keycloak
- Un filtre supplémentaire est nécessaire pour une gestion des droits globale








<!-- .slide: class="slide" -->
### Adapter Keycloak : filter Keycloak
Obtenir le jeton
```java
KeycloakSecurityContext sc = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
```
<!-- hors code car fait planter les chevrons font planter a priori highlight.js -->
Raccourci pour :
KeycloakPrincipal&lt;RefreshableKeycloakSecurityContext&gt; token = (KeycloakPrincipal&lt;RefreshableKeycloakSecurityContext&gt;) request.getUserPrincipal();

Puis :
```java
AccessToken accessToken = sc.getToken();
```







<!-- .slide: class="slide" -->
### Gestion des droits
Par filtre
- Les groupes applicatifs sont directement ajoutés aux roles Keycloak, ainsi on peut utiliser request.isuserinRole("groupe") pour vérifier
- Dans tous les cas on peut directement lire le jeton si l'autorisation dépend d'un attribut atypique.

```java
// Cas classique de groupes
sc.getToken().getRealmAccess().getRoles().contains("monGroupeApplicatif");
// Cas particulier, attributHabilitation contient une liste d'habilitation
List<String> attributHabilitation = (List<String>) sc.getToken().getOtherClaims().get("attributHabilitation");
attributHabilitation.contains("monHabilitation");
```







<!-- .slide: class="slide" -->
### Connexion à un web service, authentifié par un jeton
Obtenir le jeton pour se connecter à un Web Service
- `getTokenString();` permet de récupérer le jeton brut afin d'être retransmis à un autre service
- Il faut ensuite ajouter l'entête "Authorization: Bearer tokenString" pour accéder au WS
- Un test peut être fait sur le WS embarqué de keycloak : https://auth.insee.test/auth/realms/formation-secu-applicative/protocol/openid-connect/userinfo








<!-- .slide: class="slide" -->
### Logout
- Déconnexion locale : `request.getSession().invalidate();`
- Déconnexion Keycloak : un appel sur https://auth.insee.test/auth/realms/formation-secu-applicative/protocol/openid-connect/logout?redirect_uri=https://localhost:8443/ logout l'utilisateur sur Keycloak et redirige vers "redirect_uri"









<!-- .slide: class="slide" -->
### Environnement de production
- Il faut être capable d'adapter facilement l'application au différents environnements : local, dv/qf, prod
- Il y a une conf commune localhost par realm (une confidential, une public)
- Une demande doit être faite pour les environnement de dv/qf : une seule configuration pour tous les environnements dv/qf
- Une demande doit être faite pour la prod : en cas de confidential, seule la prod aura accès au secret








<!-- .slide: class="slide" -->
### Environnement de production
- Le json peut être paramétré pour s'adapter à des propriétés systèmes (avec valeurs par défaut sur localhost par exemple)
```json
{
  "realm": "${fr.insee.keycloak.realm:agents-insee-interne}",
  "auth-server-url": "${fr.insee.keycloak.server:https://auth.insee.test/auth}",
  "ssl-required": "none",
  "resource": "${fr.insee.keycloak.resource:localhost-web}",
  "credentials": {
    "secret": "${fr.insee.keycloak.credentials.secret:b1837f8f-e7a2-4bbc-b4bf-06a3ad5679c3}"
  },
  "confidential-port": 0,
  "principal-attribute": "preferred_username"
}
```
- Il faut demander au CEI de rajouter ces variables au démarrage de la jvm
- [Doc sur le wiki](https://git.stable.innovation.insee.eu/outils-transverses/authentification/keycloack/keycloak-insee-ext/wikis/D%C3%A9veloppeurs/G%C3%A9rer-diff%C3%A9rentes-configurations-sur-les-environnements)







<!-- .slide: class="slide" -->
### OIDC et Spring security







<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Dépendances (pour Spring boot) :
```xml
</dependencies>
<!-- Adapter Keycloak pour OpenIDConnect -->
<dependency>
  <groupId>org.keycloak</groupId>
  <artifactId>keycloak-spring-boot-starter</artifactId>
</dependency>
</dependencies>

<dependencyManagement>
  <dependencies><dependency>
    <groupId>org.keycloak.bom</groupId>
    <artifactId>keycloak-adapter-bom</artifactId>
    <version>7.0.0</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency></dependencies>
</dependencyManagement>
```







<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Dépendances (sans Spring boot) :
```xml
</dependencies>
<!-- Adapter Keycloak pour OpenIDConnect -->
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-security-adapter</artifactId>
    <version>7.0.0</version>
</dependency>
</dependencies>
```








<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Dépendance de spring security obligatoire (+web) :
```xml
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```








<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Configuration particulière de spring security
```java
@KeycloakConfiguration
public class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {
    /**
  	 * Defines the session authentication strategy.
  	 */
  	@Bean
  	@Override
  	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
      // applis frontend
  		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
      //applis bearer-only (ws)
		  return new NullAuthenticatedSessionStrategy();
  	}

  	/**
  	 * Registers the KeycloakAuthenticationProvider with the authentication manager.
  	 */
  	@Autowired
  	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
  		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
  		// simple Authority Mapper to avoid ROLE_ (on peut aussi simplement utiliser les authorities)
  		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
  		auth.authenticationProvider(keycloakAuthenticationProvider);
  	}
    ...
  }
```






<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
```java
...
  	/**
  	 * Required to handle spring boot configurations
  	 */
  	@Bean
  	public KeycloakConfigResolver KeycloakConfigResolver() {
  		return new KeycloakSpringBootConfigResolver();
  	}

  	@Override
  	protected void configure(HttpSecurity http) throws Exception {
  		// Configuration générales Keycloak
  		super.configure(http);

      // On peut rajouter des configurations générales Spring security sur http
  	}
}
```







<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Configuration via SpringBoot

```
keycloak.auth-server-url=https://auth.insee.test/auth
keycloak.realm=formation-secu-applicative
keycloak.resource=client-test-web
keycloak.credentials.secret=1a5b0b89-c23a-4d3b-9653-72faf8754a61

#keycloak.bearer-only=true
```










<!-- .slide: class="slide" -->
### Logout
- Avec l'adapter Keycloak Spring security, on peut déléguer le logout à Spring security :
 ```java
http.logout().addLogoutHandler(keycloakLogoutHandler()).logoutUrl("/logout").logoutSuccessUrl("/")
 ```
- Par défaut Spring security active une protection contre csrf, il faut par conséquent utiliser un POST et non un GET pour appeler le logout :

```html
<c:url var="logoutUrl" value="/logout"/>
<form:form action="${logoutUrl}"
  method="post">
<input type="submit"
  value="Se déconnecter" />
<input type="hidden"
  name="${_csrf.parameterName}"
  value="${_csrf.token}"/>
</form:form>
```








<!-- .slide: class="slide" -->
### Adapter Keycloak spring security
Obtenir le jeton
```java
KeycloakSecurityContext sc = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
```

Raccourci pour :
```java
KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
SimpleKeycloakAccount details = (SimpleKeycloakAccount) token.getDetails();
KeycloakSecurityContext sc = details.getKeycloakSecurityContext();
```

puis :
```java
AccessToken accessToken = sc.getToken();
```







<!-- .slide: class="slide" -->
### Environnement de production
- Sans spring boot : comme avec filter
- Avec spring boot : gestion par properties







<!-- .slide: class="slide" -->
### OIDC en JavaScript








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
- Une possibilité simple : utiliser l'adapter keycloak directement disponible sur le serveur Keycloak en https://auth.insee.test/auth/js/keycloak.js
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
	"auth-server-url": "https://auth.insee.test/auth"
}
```
- ou Keycloak('chemin vers fichier json')
- Configuration sans fichier annexe
```javascript
var keycloak = Keycloak({
	url : 'https://auth.insee.test/auth',
	realm : 'formation-secu-applicative',
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
         url: 'https://auth.insee.test/auth',
         realm: 'agents-insee-interne',
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
### Cas particuliers










<!-- .slide: class="slide" -->
### France Connect
- Le dispositif France Connect utilise OpenIdConnect
- L'inscription à France Connect n'est pas une opération anodine
- Il est pour le moment prévu de réaliser une unique inscription à France Connect pour L'Insee via Keycloak
- France connect sera proposé comme Fournisseur d'identité pour Keycloak









<!-- .slide: class="slide" -->
### Besoin de plusieurs realms
2 cas :
- Le deuxième realm est un realm secondaire :
*une application ouverte sur internet dédié aux agents Insee doit être accessible par formulaire depuis Internet mais par Kerberos en interne : le realm agents-insee-interne et une alternative à insee-ssp*
- Les différents realms sont indépendant :
*une application transverse sur laquelle peuvent se connecter divers acteurs issus d'application différentes*







<!-- .slide: class="slide" -->
### Cas d'un realm secondaire
- Similaire à une situation s'authentifier avec Google, Facebook,...
- Un fournisseur d'identité est créé côté Keycloak : *auth.insee.fr/.../agents-insee-interne devient fourniseur d'identité pour auth.insee.net/.../insee-ssp*
- Côté application, seul le realm principal doit être déclaré
- Remarque : la déclaration de fournisseur d'identité est en OpenIDConnect si les deux realms sont sur le même serveur, en SAML s'ils sont sur interne et DMZ, une question de flux.








<!-- .slide: class="slide" -->
### Cas d'un realm secondaire
il est possible de forcer keycloak à utiliser un fournisseur de service secondaire, par exemple si un discovery service est utilisé pour savoir où se situe l'utilisateur :

Applis web :
```
GET /myapplication.com?kc_idp_hint=sso-insee
```

Applis JS :
```js
var keycloak = new Keycloak('keycloak.json');
keycloak.login({
	idpHint: 'sso-insee'
});
```









<!-- .slide: class="slide" -->
### Cas de plusieurs realms indépendants
- Le filtre Keycloak propose une interface pour founir dynamiquement une configuration keycloak
- https://www.keycloak.org/docs/latest/securing_apps/index.html#_multi_tenancy
- Déclaration d'une classe resolver de conf








<!-- .slide: class="slide" -->
### Cas de plusieurs realms indépendants
```java
public class MyKeycloakConfigResolver implements KeycloakConfigResolver {
  @Override
  public KeycloakDeployment resolve(Request facade) {
    if (facade.getCookie("realm") != null) {
      String realmDemande = facade.getCookie("realm").getValue();
      InputStream is;
      if (realmDemande.equals("keycloak1")) {
        is = getClass().getClassLoader().getResourceAsStream("keycloak1.json");
      } else {
        is = getClass().getClassLoader().getResourceAsStream("keycloak2.json");
      }
      return KeycloakDeploymentBuilder.build(is);
    } else {
      return null;
    }
  }
}
```









<!-- .slide: class="slide" -->
### Cas de plusieurs realms indépendants
```xml
	<filter>
		<filter-name>Keycloak Filter</filter-name>
		<filter-class>org.keycloak.adapters.servlet.KeycloakOIDCFilter</filter-class>
		<init-param>
			<param-name>keycloak.config.resolver</param-name>
			<param-value>fr.insee.demo.multi.tenant.MyKeycloakConfigResolver</param-value>
		</init-param>
	</filter>
  ```

