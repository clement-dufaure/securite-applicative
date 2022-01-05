<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 3.1
### Authentification avec Java







<!-- .slide: class="slide" -->
### Implémentation de l'authentification en Java
- Consiste à mettre un objet dans le "user principal" de la request
- Le principal doit hériter de la classe abstraite "Principal"
- Chaque implémentation de l'authentification a sa classe de principal









<!-- .slide: class="slide" -->
### Implémentation de l'authentification
3 grands types de solution :
- Pur Java EE (Jakarta EE) : le filtre
- Basé sur le container (valve Tomcat)
- Spring security








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Filter
- Se définit dans web.xml
- Se lance avant chaque requête correspondant au filter-mapping

```xml
<filter>
  <filter-name>Keycloak Filter</filter-name><!-- Identifiant du filtre-->
  <filter-class>org.keycloak.adapters.servlet.KeycloakOIDCFilter</filter-class>
  <!-- Classe d'appel qui doit implémenter l'interface Filter : méthode "invoke()" -->
</filter>
<filter-mapping><!-- Définition des urls où le filtre va s'appliquer -->
  <filter-name>Keycloak Filter</filter-name>
  <url-pattern>/private/*</url-pattern>
</filter-mapping>
```

- Le traitement de l'authentification pour les droits d'accès peut se faire dans un autre filtre (ou dans le code)






<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Filter
En Spring, on peut simplement définir un filtre
```java
@Filter
@Order(n)
public class MyFilter implements Filter{
}
```






<!-- .slide: class="slide" -->
### Implémentation de l'authentification
- Les implémentations d'authentification sont censées placer certaines informations dans l'objet de requete.
- Quelques méthodes de l'interface HTTPServletRequest :
   - getRemoteUser, getUserPrincipal pour vérifier que l'utilisateur est authentifié (non null si authentifié)
   - isUserInRole pour vérifier que l'utilisateur a un droit donné
   - isSecure pour vérifier que la connexion réseau est sécurisée





<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Spring security
- Plus lourd en dépendances
- Nécessite un base Spring ou Spring Boot
- plus facile sur un projet neuf : créer un modèle complet de dépendances sur start.spring.io







<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Spring security
- Dépendances SANS Spring Boot

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>--last--</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>--last--</version>
</dependency>
```






<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Spring security
- Dépendances AVEC Spring Boot

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
### Implémentation de l'authentification
Spring security
- Configuration dans une classe dédiée

```java
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
  ...
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    ...
  }
}
```








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
L'objet HttpSecurity permet de configurer les comportements de sécurité
- Fontionne avec des méthode "and()" pour faire suivre les instructions
- Ajout des filtres d'authentification via les méthodes addFilterBefore addFilterAfter
- Définition page erreur 403 : 

```java
.exceptionHandling().accessDeniedPage("/accessDenied")
```

- Vérifier le HTTPS : 

```java
.requiresChannel().antMatchers("/**").requiresSecure()
```

- Vérifier les droits : 

```java
authorizeRequests().antMatchers("/public/**").permitAll().antMatchers("/private/**").authenticated().antMatchers("/admin/**").hasRole("admin")
```

  - permitAll() = accès public
  - authenticated() = utilisateurs authentifié sans controle de role
  - hasRole("admin") ou hasAuthority("admin") = utilisateurs authentifiés avec le rôle "admin"








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
- Principe Authority/Role
- En spring security, on ajoute des authority
- Si l'authority commence pas "ROLE_", elle est considérée comme rôle par Spring
- Dans ce cas lors d'un "hasRole()", il faut rechercher uniquement "monroleapplicatif" et non pas "ROLE_monroleapplicatif"
- Intérêt de role ? 
  - Spring Security n'ajoutera que les ROLE_* dans les role de l'objet HttpRequest
  - Donc les méthodes l'utilisant (isUserInRole par exemple) ne marcheront que dans ces conditions







<!-- .slide: class="slide" -->
### Implémentation de l'authentification
- Exemple : Ajout dans Spring Security des authorities "ROLE_utilisateur" et "user"
- L'utilisateur A possède "ROLE_utilisateur" et l'utilisateur B "user"
- `.antMatchers("/private/**").hasAuthority("utilisateur")` autorisera l'accès à aucun de ces utilisateurs
- `.antMatchers("/private/**").hasAuthority("ROLE_utilisateur")` autorisera l'accès à l'utilisateur A
- `.antMatchers("/private/**").hasAuthority("user")` autorisera l'accès à l'utilisateur B
- `.antMatchers("/private/**").hasRole("utilisateur")` autorisera l'accès à l'utilisateur A
- `.antMatchers("/private/**").hasRole("user")` autorisera l'accès à aucun de ces utilisateurs







<!-- .slide: class="slide" -->
### Tests avec Spring Security
Idée : mocker un utilisateur lors d'un cas de test
- Lui affecter une identité
- Lui affecter des rôles

Srping Security test utilise uniquement l'interface Principal de JakartaEE, on ne pourra pas faire mieux que mocker nom et role (en particulier pas de "mock token")









<!-- .slide: class="slide" -->
### Tests avec Spring Security
```java
// Runner Spring si Junit 4 (lance un serveur pour les tests)
@RunWith(SpringRunner.class)
//Runner Spring si Junit 5 (lance un serveur pour les tests)
@ExtendWith(SpringExtension.class)

// Prepare la conf MVC de la classe en paramètre
@WebMvcTest(Controlleur.class)

// Specifie tous les beans a charger
@ContextConfiguration(classes = { Controlleur.class, KeycloakConfigurationAdapter.class,
		KeycloakBaseSpringBootConfiguration.class, KeycloakSpringBootProperties.class })
public class AuthenticationTest {
```







<!-- .slide: class="slide" -->
### Tests avec Spring Security
La dépendance spring-security-test propose une annotation @WithMockUser.

Elle permet de peupler le Principal de la requete.

Se limite à l'interface de principal : 
- possibilité de donner un nom (Principal.getName) 
  - attribut "username"
- possibilité de donner des roles (HttpServletRequest.isUserInRole) 
  - attribut "roles"






<!-- .slide: class="slide" -->
### Tests avec Spring Security
```java
	@Test
	@WithMockUser(username = "Alfred Robichu", roles = { "administrateur" })
	public void recupererPageAdminEnEtantAdmin() throws Exception {
		mvc.perform(get("/admin").secure(true)).andExpect(status().isOk())
      .andReturn();
	}
```