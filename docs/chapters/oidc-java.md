<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 4.1
### OpenID Connect et Java







<!-- .slide: class="slide" -->
### Adapter Keycloak
- Les adapters Java

https://www.keycloak.org/docs/latest/securing_apps/#java-adapters

- Adapters liés à une plateforme fortement déconseillés, car ils créent des dépendances "Hors Maven"








<!-- .slide: class="slide" -->
### Mise en place de OpenIDConnect dans une application Java
- Avec le filter keycloak
- Avec l'adapter Keycloak Spring security







<!-- .slide: class="slide" -->
### OIDC et filtre Jakarta EE







<!-- .slide: class="slide" -->
### Filtre Keycloak
Dépendances :
```xml
<dependency>
			<groupId>org.keycloak</groupId>
			<artifactId>keycloak-servlet-filter-adapter</artifactId>
			<version>12.0.2</version>
</dependency>
```







<!-- .slide: class="slide" -->
### Filtre Keycloak
Ajout du filtre dans web.xml
```xml
<filter>
	<filter-name>Keycloak Filter</filter-name>
	<filter-class>org.keycloak.adapters.servlet.KeycloakOIDCFilter</filter-class>
	<init-param>
		<param-name>keycloak.config.skipPattern</param-name>
		<param-value>^/healthcheck</param-value>
	</init-param>
</filter>
<filter-mapping>
	<filter-name>Keycloak Filter</filter-name>
	<url-pattern>/private/*</url-pattern>
	<url-pattern>/admin/*</url-pattern>
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
- Un test peut être fait sur le WS embarqué de keycloak : https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/userinfo








<!-- .slide: class="slide" -->
### Logout
- Déconnexion locale : `request.getSession().invalidate();`
- Déconnexion Keycloak : un appel sur https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/logout?redirect_uri=https://localhost:8443/ logout l'utilisateur sur Keycloak et redirige vers "redirect_uri"









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
  "realm": "${fr.insee.keycloak.realm:formation}",
  "auth-server-url": "${fr.insee.keycloak.server:https://mon.serveur.keycloak/auth}",
  "ssl-required": "none",
  "resource": "${fr.insee.keycloak.resource:localhost-web}",
  "credentials": {
    "secret": "${fr.insee.keycloak.credentials.secret:abcd_1234_abcd}"
  },
  "confidential-port": 0,
  "principal-attribute": "preferred_username"
}
```

- Il faut demander au CEI de rajouter ces variables au démarrage de la jvm







<!-- .slide: class="slide" -->
### OIDC et Spring security







<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Dépendances :
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
    <version>12.0.2</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency></dependencies>
</dependencyManagement>
```








<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Dépendance de spring security obligatoire :
```xml
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
keycloak.auth-server-url=https://mon.serveur.keycloak/auth
keycloak.realm=formation
keycloak.resource=client-test-web
keycloak.credentials.secret=1a5b0b89-c23a-4d3b-9653-72faf8754a61

#keycloak.bearer-only=true
```










<!-- .slide: class="slide" -->
### Logout
- Avec l'adapter Keycloak Spring security, on peut déléguer le logout à Spring security, qui ne fonctionne que en post :

```java
http.logout().addLogoutHandler(keycloakLogoutHandler()).logoutUrl("/logout").logoutSuccessUrl("/")
```

- Par défaut Spring security active une protection contre csrf, il faut par conséquent adapter le POST :

```html
    <form action="#" th:action="@{/logout}" method="post">
        <input type="submit" value="Se déconnecter" />
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    </form>
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
- La gestion se fait par properties








<!-- .slide: class="slide" -->
### Moins dépendre de Keycloak
- Spring security intégre un support générique à OAuth et OIDC
- Plus complexe à coder (les adapters Keycloak font énormément de raccourcis sur la constitution du jeton, ...)
- Assez compliqué en mode Front Web, plus abouti en mode Ressource Server
- Cf projet application-basique-ws 
