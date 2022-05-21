<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 4.1
### OpenID Connect et Java

-----

<!-- .slide: class="slide" -->
### Adapter Keycloak
- Les adapters Java

Une sélection : https://oauth.net/code/java/

A ne plus utiliser car déprécié par la communauté :
https://www.keycloak.org/docs/latest/securing_apps/#java-adapters

- Adapters liés à une plateforme fortement déconseillés, car ils créent des dépendances "Hors Maven"

-----

<!-- .slide: class="slide" -->
### Mise en place de OpenIDConnect dans une application Java
- Avec pac4j https://www.pac4j.org/ 
	- Situation application quelconque JavaEE, application de type ihm en java
- Avec Spring security
	- Situation application Spring Boot, application de type Web Service

-----

<!-- .slide: class="slide" -->
### OIDC et Pac4j

-----

<!-- .slide: class="slide" -->
### Pac4j
Dépendances modules jee + oidc:
```xml
		<dependency>
			<groupId>org.pac4j</groupId>
			<artifactId>jee-pac4j</artifactId>
			<version>6.1.0</version>
		</dependency>

		<dependency>
			<groupId>org.pac4j</groupId>
			<artifactId>pac4j-oidc</artifactId>
			<version>5.4.3</version>
		</dependency>
```
-----

<!-- .slide: class="slide" -->
### Pac4j
- Une classe de configuration

```java
public class DemoConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {

    }

}
```

-----

<!-- .slide: class="slide" -->
- Définition des protocoles d'authentification
```java
  OidcConfiguration oidcConfig = new OidcConfiguration();
        oidcConfig.setClientId(clientId);
        oidcConfig.setSecret(clientSecret);
        oidcConfig.setDiscoveryURI(configurationEndpoint);
        OidcClient oidcClient = new OidcClient(oidcConfig);
```

-----

<!-- .slide: class="slide" -->
- Définition de l'url de callback (= ou est ce que l'on revient après authentification sur le serveur central)
```java
oidcClient.setCallbackUrl("/callback");
```

-----

<!-- .slide: class="slide" -->
- Ce endpoint doit être intercepté
```xml
	<filter>
		<filter-name>callbackFilter</filter-name>
		<filter-class>org.pac4j.jee.filter.CallbackFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>callbackFilter</filter-name>
		<url-pattern>/callback</url-pattern>
		<dispatcher>REQUEST</dispatcher>
	</filter-mapping>
```


-----

<!-- .slide: class="slide" -->
### Pac4j
Définition de la liste de client (plusieurs mode d'authentification possible potentiellement : oidc + basic par exemple)
```java
Config config = new Config(oidcClient);
```


-----

<!-- .slide: class="slide" -->
- Définition de règles et conditions

```java
config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
config.addAuthorizer("mustBeAuthent",new IsAuthenticatedAuthorizer());

config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^\\/(accueil)?$"));
```


-----

<!-- .slide: class="slide" -->
- Activables selon les endpoints
```xml
	<filter>
		<filter-name>oidcFilter</filter-name>
		<filter-class>org.pac4j.jee.filter.SecurityFilter</filter-class>
		<init-param>
			<param-name>configFactory</param-name>
			<param-value>fr.insee.demo.security.DemoConfigFactory</param-value>
		</init-param>
		<init-param>
			<param-name>authorizers</param-name>
			<param-value>mustBeAuthent,csrfToken</param-value>
		</init-param>
		<init-param>
			<param-name>matchers</param-name>
			<param-value>excludedPath</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>oidcFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
```

-----

<!-- .slide: class="slide" -->
### Adapter Keycloak : filter Keycloak
Obtenir le jeton

```java
WebContext context = new JEEContext(request, response);
ProfileManager manager = new ProfileManager(context,JEESessionStore.INSTANCE);
Optional<UserProfile> profile = manager.getProfile();
OidcProfile oidcProfile = (OidcProfile) profile.get();
Map h = oidcProfile.getAttributes();
```
- Il faut connaitre le nom des claims souhaités :

```java
h.get("email")
```

-----
<!-- .slide: class="slide" -->
### Gestion des droits
Pour mapper des roles, il faut implémenter selon le besoin un générateur de roles à partir du profile (accessToken notemment)
```java
oidcClient.addAuthorizationGenerator(new AuthorizationGenerator() {
	@Override
	public Optional<UserProfile> generate(WebContext context,
													  SessionStore sessionStore,
													  UserProfile profile) {
		return Optional.empty();
	}
});
```
-----

<!-- .slide: class="slide" -->
### Gestion des droits

Peut s'utiliser dans le code directement
```java
oidcProfile.getRoles();
request.isUserInRole("admin");
```
ou géré par règles dans la config
```java
config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("admin"));
```

-----


<!-- .slide: class="slide" -->
### Gestion des droits
Possiblité de l'implémentation keycloak incluse
```java
var profileCreator = new OidcProfileCreator(oidcConfig, oidcClient);
profileCreator.setProfileDefinition(
		new OidcProfileDefinition(x -> new KeycloakOidcProfile()));
oidcClient.setProfileCreator(profileCreator);
oidcClient.addAuthorizationGenerator(
		new KeycloakRolesAuthorizationGenerator());
```


-----

<!-- .slide: class="slide" -->
### Connexion à un web service, authentifié par un jeton
Obtenir le jeton pour se connecter à un Web Service
```java
oidcProfile.getAccessToken().value()
```
- Il faut ensuite ajouter l'entête "Authorization: Bearer tokenString" pour accéder au WS
```java
oidcProfile.getAccessToken().toAuthorizationHeader()
```
- Un test peut être fait sur le WS embarqué de keycloak : https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/userinfo

-----

<!-- .slide: class="slide" -->
### Logout
Concrètement, il s'agit d'une :
- Déconnexion locale : `request.getSession().invalidate();`
- Déconnexion Keycloak : un appel sur https://mon.serveur.keycloak/auth/realms/formation/protocol/openid-connect/logout?redirect_uri=https://localhost:8443/ logout l'utilisateur sur Keycloak et redirige vers "redirect_uri"

-----

<!-- .slide: class="slide" -->
```xml
	<!-- Logout configuration -->
	<filter>
		<filter-name>logoutFilter</filter-name>
		<filter-class>org.pac4j.jee.filter.LogoutFilter</filter-class>
		<init-param>
			<param-name>centralLogout</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>logoutUrlPattern</param-name>
			<param-value>.*</param-value>
		</init-param>
		<init-param>
			<param-name>defaultUrl</param-name>
			<param-value>http://localhost:8080/</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>logoutFilter</filter-name>
		<url-pattern>/logout</url-pattern>
	</filter-mapping>
```

-----

<!-- .slide: class="slide" -->
## Bonus : CSRF
Config :
```java
config.addAuthorizer("csrfToken", new CsrfAuthorizer());
```
Controller :
```java
var gen = new DefaultCsrfTokenGenerator();
var csrfToken = gen.get(context, JEESessionStore.INSTANCE);
model.addAttribute("_csrf_token_name", Pac4jConstants.CSRF_TOKEN);
model.addAttribute("_csrf_token", csrfToken);
```

-----

<!-- .slide: class="slide" -->
Ecran :
```html
<form th:action="@{/private}" method="POST">
    <input type="hidden" th:value="${_csrf_token}" th:name="${_csrf_token_name}"/>
    <input type="submit" value="C'est parti !!" />
</form>
```

-----
<!-- .slide: class="slide" -->
### Environnement de production
- Il faut être capable d'adapter facilement l'application au différents environnements : local, dv/qf, prod
- Il y a une conf commune localhost par realm (une confidential, une public)
- Une demande doit être faite pour les environnement de dv/qf : une seule configuration pour tous les environnements dv/qf
- Une demande doit être faite pour la prod : en cas de confidential, seule la prod aura accès au secret

-----
<!-- .slide: class="slide" -->
### Environnement de production -- Cas du filtre Keycloak
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

-----

<!-- .slide: class="slide" -->
### OIDC et Spring security

-----

<!-- .slide: class="slide" -->
### Adapter Spring security module oAuth2
Dépendances :
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

-----

<!-- .slide: class="slide" -->
### Adapter Spring security module oAuth2
Configuration particulière de spring security
```java
public class MySecurityConfigurationAdapter
		  extends WebSecurityConfigurerAdapter {
		
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	}
  }
```

-----
<!-- .slide: class="slide" -->
### Adapter Spring security module oAuth2
```java
// web service = pas de gestion de session
http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
// pas de formulaire => pas de csrf
http.csrf().disable();
// gestion des accès
http.authorizeRequests(
			authz -> /* Fonction des droits */)
				  .oauth2ResourceServer();
```

-----
<!-- .slide: class="slide" -->
Fonction de droits

```java
authz -> 
	authz.antMatchers(HttpMethod.GET, "/private")  // CONDITION
			.authenticated(). // UTILISATEUR AUTHENTIFIE SANS CONTROLE

		antMatchers(HttpMethod.GET,"/admin") // CONDITION
			.hasRole("admin") // CONTROLE DU ROLE

		.antMatchers(HttpMethod.OPTIONS).permitAll() // pour les requetes CORS
		.antMatchers("error", "/","/accueil").permitAll() // Chemins publics
		.anyRequest().denyAll()  // Dans le doute le reste est interdit
```


-----
<!-- .slide: class="slide" -->
### Adapter Keycloak Spring security
Configuration via SpringBoot
- Seul l'emplacement des certificat est nécessaire.
La conf se retrouve ici : http://localhost:8180/auth/realms/test/.well-known/openid-configuration


```
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/auth/realms/test/protocol/openid-connect/certs
```


-----


<!-- .slide: class="slide" -->
### Recherche des roles
- Comme souvent, se réimplémente !

```java
http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {
					  jwtConfigurer.jwtAuthenticationConverter(
								 jwtAuthenticationConverter());
				  }));
```

-----
<!-- .slide: class="slide" -->
```java
JwtAuthenticationConverter jwtAuthenticationConverter() {
	JwtAuthenticationConverter jwtAuthenticationConverter =
			  new JwtAuthenticationConverter();
	jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
			  jwtGrantedAuthoritiesConverter());
	jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
	return jwtAuthenticationConverter;
}
```

-----
<!-- .slide: class="slide" -->
```java
Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
	return new Converter<Jwt, Collection<GrantedAuthority>>() {
		@Override
		@SuppressWarnings({"unchecked", "serial"})
		public Collection<GrantedAuthority> convert(Jwt source) {
			return ((Map<String, List<String>>) source.getClaim(
					  "realm_access")).get("roles").stream()
					  .map(s -> new GrantedAuthority() {
						  @Override
						  public String getAuthority() {
							  return "ROLE_" + s;
						  }
						  
						  @Override
						  public String toString() {
							  return getAuthority();
						  }
					  }).collect(Collectors.toList());
		}
	};
}
```


-----
<!-- .slide: class="slide" -->
### Adapter Keycloak spring security
Obtenir le jeton (appel à web service)
```java
JwtAuthenticationToken token =
				  (JwtAuthenticationToken) request.getUserPrincipal();
maRequete.header("Authorization",
							 "Bearer " + token.getToken());
```


-----
<!-- .slide: class="slide" -->
### Dépendances adapters java Keycloak (JEE et Spring Security)
- Ces adapters étaient plus facile à configurer dans notre environnement Keycloak (prévus pour !)
- C'est toujours un plus de ne pas dépendre d'une implémentation mais d'un standard (OIDC/OAuth)
- Le projet keycloak abandonne le support des adapters Keycloak
