---
title : "L'authentification en java"
description: ""
lead: ""
draft: false
images: []
weight: 601
mermaid: true
---


## Implémentation de l'authentification en Java
- Consiste à mettre un objet dans le "user principal" de la request
- Le principal doit hériter de la classe abstraite "Principal"
- Chaque implémentation de l'authentification a sa classe de principal


3 grands types de solution :
- Pur Java EE (Jakarta EE) : le filtre
  - Projet [Pac4j](https://www.pac4j.org/)
- Basé sur le container (valve Tomcat)
- Spring security : https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html


## Filter

- Se définit dans web.xml
- Se lance avant chaque requête correspondant au filter-mapping
- Selon l'implémentation, peut contenir une gestion de role (attention à l'ordre des filtres), mais les gestions les plus fines peuvent se faire dans un autre filtre, voire directement dans le code


exemple du filtre pac4j :

```xml
	<filter>
		<filter-name>zoneAuthentifieeFilter</filter-name>
		<filter-class>org.pac4j.jee.filter.SecurityFilter</filter-class>
		<init-param>
			<param-name>configFactory</param-name>
			<param-value>fr.insee.demo.security.DemoConfigFactory</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>zoneAuthentifieeFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
```

(pac4j fonctionne avec l'écriture d'une classe de configuration)


*Remarque* : En Spring, on peut simplement définir un filtre
```java
@Filter
@Order(n)
public class MyFilter implements Filter{
}
```

- Principe Java EE : requête entrante matérialisée par un objet `HTTPServletRequest`
- Les implémentations d'authentification sont censées placer certaines informations dans cet objet de requete.


- Quelques méthodes de l'interface HTTPServletRequest :
   - getRemoteUser, getUserPrincipal pour vérifier que l'utilisateur est authentifié (non null si authentifié)
   - isUserInRole pour vérifier que l'utilisateur a un droit donné
   - isSecure pour vérifier que la connexion réseau est sécurisée


### Spring security
- Plus lourd en dépendances
- Nécessite une base Spring ou Spring Boot
- plus facile sur un projet neuf : créer un modèle complet de dépendances sur start.spring.io
- ajout du module "security", ainsi que des sous modules correspondant à l'auhentification souhaitée (ldap, oauth, ...)

Spring security
- Configuration dans une classe dédiée

exemple d'une conf web service acceptant simultanément l'authentification basic et oauth

```java
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

@Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Cas d'un webservice :
    // - ne pas gerer les sessions
    // - pas de csrf
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.csrf().disable();
    // Descriptions des regles d'accès par path
    http.authorizeHttpRequests(
        requests ->
            requests
                // Ignorer le path du swagger
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/healthcheck")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/private")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/admin")
                .hasRole("admin")
                .requestMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .anyRequest()
                .denyAll());
    // Moyens d'authentification : Basic + oAuth2 bearer
    http.httpBasic();
    http.oauth2ResourceServer();
    return http.build();
  }

  // CONFIGURATION BASIC
  // Ou vérifier les credentials passés en basic ?
  // ici : users de test en mémoire (PAS PRODUCTION READY :D)

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user").password("{noop}password").roles("").build();
    UserDetails admin =
        User.withUsername("admin").password("{noop}password").roles("ADMIN").build();
    return new InMemoryUserDetailsManager(user, admin);
  }

}
```

L'objet `HttpSecurity http` permet de configurer les comportements de sécurité

- Vérifier le HTTPS : 

```java
.requiresChannel().antMatchers("/**").requiresSecure()
```

- Définition page erreur 403 : 
```java
.exceptionHandling().accessDeniedPage("/accessDenied")
```


- Vérifier les droits : 
```java
authorizeRequests().antMatchers("/public/**").permitAll().antMatchers("/private/**").authenticated().antMatchers("/admin/**").hasRole("admin")
```

  - permitAll() = accès public
  - authenticated() = utilisateurs authentifié sans controle de role
  - hasRole("admin") ou hasAuthority("admin") = utilisateurs authentifiés avec le rôle "admin"


- Principe Authority/Role
- En spring security, on ajoute des authority
- Si l'authority commence pas "ROLE_", elle est considérée comme rôle par Spring

- Dans ce cas lors d'un "hasRole()", il faut rechercher uniquement "monroleapplicatif" et non pas "ROLE_monroleapplicatif"
- Intérêt de role ? 
  - Spring Security n'ajoutera que les ROLE_* dans les role de l'objet HttpRequest
  - Donc les méthodes l'utilisant (isUserInRole par exemple) ne marcheront que dans ces conditions


- Exemple : Ajout dans Spring Security des authorities "ROLE_utilisateur" et "user"
- L'utilisateur A possède "ROLE_utilisateur" et l'utilisateur B "user"
- `.antMatchers("/private/**").hasAuthority("utilisateur")` autorisera l'accès à aucun de ces utilisateurs
- `.antMatchers("/private/**").hasAuthority("ROLE_utilisateur")` autorisera l'accès à l'utilisateur A
- `.antMatchers("/private/**").hasAuthority("user")` autorisera l'accès à l'utilisateur B
- `.antMatchers("/private/**").hasRole("utilisateur")` autorisera l'accès à l'utilisateur A
- `.antMatchers("/private/**").hasRole("user")` autorisera l'accès à aucun de ces utilisateurs


### Tests avec Spring Security
Idée : mocker un utilisateur lors d'un cas de test
- Lui affecter une identité
- Lui affecter des rôles

Srping Security test utilise uniquement l'interface Principal de JakartaEE, on ne pourra pas faire mieux que mocker nom et role (en particulier pas de "mock token")

```java
// Runner Spring si Junit 4 (lance un serveur pour les tests)
@RunWith(SpringRunner.class)
//Runner Spring si Junit 5 (lance un serveur pour les tests)
@ExtendWith(SpringExtension.class)

// Prepare la conf MVC de la classe en paramètre
@WebMvcTest(Controlleur.class)
public class AuthenticationTest {

	@Autowired
	public MockMvc mvc;
```


La dépendance spring-security-test propose une annotation @WithMockUser.

Elle permet de peupler le Principal de la requete.

Se limite à l'interface de principal : 
- possibilité de donner un nom (Principal.getName) 
  - attribut "username"
- possibilité de donner des roles (HttpServletRequest.isUserInRole) 
  - attribut "roles"


```java
	@Test
	@WithMockUser(username = "Alfred Robichu", roles = { "administrateur" })
	public void recupererPageAdminEnEtantAdmin() throws Exception {
		mvc.perform(get("/admin").secure(true)).andExpect(status().isOk())
      .andReturn();
	}
```

