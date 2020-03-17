<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 3
### Authentification





<!-- .slide: class="slide" -->
### Qu'est qu'une personne authentifiée
- Une personne connue de l'application
- Elle est donc enregistrée
- Comment stocker cette information ? Type de base de donnée dédiée : l'annuaire






<!-- .slide: class="slide" -->
### Qu'est que l'authentification
- Validation d'une identification
- Un utilisateur doit indiquer qui il est (identifiant, certificat client) et le prouver (mot de passe, certifcat client, carte à puce, code à usage unique, empreinte digitale)
- Au moins un facteur, mais plusieurs facteurs simultanés améliorent la preuve (mot de passe + carte à puce)





<!-- .slide: class="slide" -->
### Le SSO - Single Sign On
- S'identifier une seule fois pour accéder à plusieurs services
- Système d'authentification centralisé
- Question du SLO
  - Logout = déconnexion de l'application uniquement ?
  - Ou déconnexion de l'application et du fournisseur d'identité ?
  - Ou encore, de l'application, du fournisseur, et de toutes les autres applis s'étant connectées via le fournisseur ?







<!-- .slide: class="slide" -->
### Annuaire
Référentiel permettant de stocker des données de manière hiérarchique et offrant des mécanismes pour rechercher efficacement l'information.

- Recensement des informations sur les utilisateurs, sur les applications, sur un parc informatique
- Authentifier un utilisateur
- Donner un droit d'un objet sur un autre, gestion de groupes (hierarchie)





<!-- .slide: class="slide" -->
### Technologies d'annuaires
- OpenLDAP : projet opensource
- Active Directory (AD) : solution Microsoft.






<!-- .slide: class="slide" -->
### Ressource authentifié
Les besoins d'authentification sont variés.

Une application peut proposer :
- Du contenu public (donc sans authentification)
- Du contenu accessible à n'importe quel utilisateur authentifié
- Du contenu accessible à certains utilisateurs





<!-- .slide: class="slide" -->
### Ressource authentifié
- Authentification explicite : je présente un en-tête "Authorization" dans ma requête
- Authentification implicite : je m'attends à ce que l'application me donne un moyen de m'authentifier







<!-- .slide: class="slide" -->
### Ressource authentifié
En arrivant sur une page où je suis censé être authentifié
- Je présente un header "Authorization" ?
- Si oui et que l'application l'accepte je suis authentifié
- Sinon, l'application peut me proposer des moyens de m'authentifier
- S'il n'y a pas d'autres moyens que le header Authorization ou que j'ai déjà tenté tous les modes possibles sans succès, je reçois définitivement une erreur 401 "Unauthorized"






<!-- .slide: class="slide" -->
### Ressource authentifié
Si un ou plusieurs autre modes de connexion sont disponibles, les fonctionnements sont variés :
- 401 + WWW-Authenticate header
- 200 + traitements divers
- 30X : redirection vers page de login ou vers un fournisseur d'identité







<!-- .slide: class="slide" -->
### Ressource authentifié
- Exemple : l'authentification Basic

```
Authorization: Basic Base64(identifiant:motdepasse)
```

- Si un site propose l'authentification Basic, et que l'on vient sans l'entête authorisation, le site répond avec l'entête :

```
WWW-Authenticate: Basic realm="nom d'affichage"
```

- Le navigateur voyant cet entête affiche à l'utilisateur une pop-up demandant un identifiant et mot de passe et ayant pour titre le "nom d'affichage"
- Le navigateur utilisera ce couple identifiant mot de passe pour former l'en-tête Authorisation tout le reste de la session







<!-- .slide: class="slide" -->
### Ressource authentifié
Je suis correctement identifié mais la ressource ne s'adresse qu'à un certain type d'utilisateur dont je ne fais pas partie :
- Erreur 403 Forbidden







<!-- .slide: class="slide" -->
### Rappel code réponse HTTP :
- 20X : succès
- 30X : redirection
- 40X : erreur côté Client Web
- 50X : erreur côté Serveur Web






<!-- .slide: class="slide" -->
### Rappel code réponse HTTP :
- 200 : OK
- 301 : Moved Permanently
- 302 : Found (Déplacé temporairement)
- 400 : Bad Request
- 401 : Unauthorized
- 403 : Forbidden
- 404 : Not Found
- 418 : I’m a teapot ([RFC 2324](https://www.ietf.org/rfc/rfc2324.txt))
- 500 : Internal Server Error
- 503 : Service Unavailable : réponse fournie par un reverse proxy si l'application n'est pas disponible (maintenance par exemple)







<!-- .slide: class="slide" -->
### Rappel code réponse HTTP :
401 ou 403 ?
- 401 : Unauthorized : L'authentification ne s'est pas dérolée comme prévue ou elle s'est bien déroulée mais l'utilisateur est inconnu de l'application
- 403 : Forbidden : L'authentification s'est bien déroulée et l'utilisateur est connu mais il n'a pas les habilitations nécessaires








<!-- .slide: class="slide" -->
### Comment savoir si l'utilisateur a les droits ?
Le mode d'authentification peut simplement présenter une identifiation. C'est alors à moi de déterminer si l'utilisateur a le droit d'accéder à la ressource et d'afficher les bonnes informations.

*M Robichu se connecte à mon questionnaire en ligne.*

*Il a pu s'authentifier correctement sur l'application, la seule information connue de l'application est alors son identifiant.*

*L'application recherche en base de données l'identifiant de M Robichu et récupère bien une ligne qui contient notamment les informations déjà renseignées par M Robichu.*

*Je sais donc que M Robichu a les droits sur l'application et je peux en plus préremplir le questionnaire avec ce que je sais déjà*






<!-- .slide: class="slide" -->
### Comment savoir si l'utilisateur a les droits ?
Le mode d'authentification peut fournir une réponse plus complète contenant des rôles. Je peux alors me servir de ces rôles récupérés pour gérer l'accès à mes ressources.

*M Robichu se connecte à mon questionnaire en ligne.*

*Il a pu s'authentifier correctement sur l'application, les informations connues de l'application sont maintenant son identifiant mais aussi une liste de rôles dont "repondant-enquete-satisfaction-joint-etancheite-climatiseur-morgue". *

*Je sais directement que M Robichu a les droits sur l'application. Mais rien ne m'empêche de chercher en base de données des informations plus précises sur M Robichu*






<!-- .slide: class="slide" -->
### Comment savoir si l'utilisateur a les droits ?
Rôles dans l'authentification ou en base de données ?

Se limiter au rôles le plus brut possible dans le système d'authentification (répondant, administrateur)

Éviter de trop spécifier le rôle surtout si l'on possède l'information en base de données. (répondant à l'enquête X, à l'enquête Y, etc.)






<!-- .slide: class="slide" -->
### Quelques exemples d'authentification
Authentifications directes :
- Basic
- Formulaire
- [Certificat Client](https://developer.mozilla.org/fr/docs/Introduction_%C3%A0_la_cryptographie_%C3%A0_clef_publique/Certificats_et_authentification)
- Kerberos : adapté au Web par SPNEGO, Simple and Protected GSSAPI (Generic Security Services Application Program Interface) Negotiation Mechanism, implémenté par AD
- NTLM (NT Lan Manager) : Systèmes Windows ou compatible AD

Authentifications centralisées nécessitant l'utilisation d'un autre mode :
- SAML
- OpenID Connect






<!-- .slide: class="slide" -->
### Kerberos
*WWW-Authenticate: Negociate*

*Dans les grandes lignes*
- Serveur central KDC (Key Distribution Center)
- Identifiants publics transmis en clair
- Système de clé symétrique, chaque utilisateur et serveur proposant authentification en partage une avec un serveur KDC (AD dans le cas Windows)
- Clé déduite d'un hash du mot de passe de l'utilisateur
- L'utilisateur envoie son identifiant plus un message variable (timestamp) chiffré par sa clé
- Le KDC génère et envoie un {jeton chiffré par sa clé + une clé de session}, chiffré à nouveau par la clé de l'utilisateur. Ce jeton est à usage long (durée de la session Windows)










<!-- .slide: class="slide" -->
### Kerberos

*Dans les grandes lignes*
- Pour accéder à un service, le client retransmet le jeton au serveur KDC en demandant l'accès à un service
- Le KDC répond un nouveau jeton dédié au service, chiffré avec la clé du service, et chiffré à nouveau avec la clé de session
- L'utilisateur récupère le jeton avec la clé de session et le transmet au service
- Le service envoie un challenge à l'utilisateur basé sur la clé de session contenue dans le jeton

*Plus complet* : https://blog.devensys.com/kerberos-principe-de-fonctionnement/








<!-- .slide: class="slide" -->
### NTLM
*Kerberos simplifié et moins sécurisé*

*WWW-Authenticate: NegociateNTLM*
- Envoi de l'identifiant
- Challenge du serveur, à résoudre avec le hash du mot de passe
- Le serveur demande à AD de valider (identifiant,challenge, réponse du client)
- AD répond au serveur si l'authentification est OK









<!-- .slide: class="slide" -->
### Authentifications Kerberos ou NTLM : Sécurité
- Afin de contrôler ce flux, les sources autorisées à émettre une demande de challenge sont contrôlées
- Dans Firefox, il s'agit des propriétés suivantes (about:config)

```
network.negotiate-auth.delegation-uris : insee.intra,insee.fr,insee.test,insee.eu,localhost
network.negotiate-auth.trusted-uris : insee.intra,insee.fr,insee.test,insee.eu,localhost
network.automatic-ntlm-auth.trusted-uris :
```

*Plus d'informations* : https://developer.mozilla.org/en-US/docs/Mozilla/Integrated_authentication








<!-- .slide: class="slide" -->
### Challenge ?
- Principe de demander à l'utilisateur de répondre à une information par la même information chiffrée
- Challenge avec clé symétrique : Le serveur et le client connaissent la même clé, le client chiffre l'information que le serveur pourra déchiffrer avec la même clé
- Challenge avec clé asymétrique : Le client est le seul à connaître une clé privée, la clé publique est a priori connu de tous (en pratique non diffusée mais potentiellement transmise en clair). Le serveur vérifie s'il retrouve l'information en déchiffrant avec la clé publique.








<!-- .slide: class="slide" -->
### Confidentialité de l'authentification
- Kerberos, NTLM, SAML : Sécurité intrinsèque (même sur un canal en clair, une lecture réseau ne permet pas d'obtenir des informations sur les credentials de l'utilisateur)
- Certificat client : Étape du protocole HTTPS
- Formulaire, Basic, OIDC : Sécurité basée sur un canal chiffré (HTTPS)






<!-- .slide: class="slide" -->
### Utilisation d'une fédération d'identité
- On verra le fonctionnement en détail plus tard (SAML, OpenIDConnect)
- Principe : déléguer l'authentification à un autre système (Kerberos et NTLM sont au fond des fédérations d'identité)
- Intérêts :
  - Authentification centralisée
  - Éviter les accès directs aux annuaires
  - SSO
  - Amélioration de la sécurité : la sécurité est également déléguée au protocole et au système authentifiant, reduction de la surface d'attaque
  - Cependant la sécurité du client reste à la charge des applications










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
- Les implémentations d'authentification sont censées placer certaines informations dans l'objet de requete.
- Quelques méthodes de l'interface HTTPServletRequest :
   - getremoteuser, getuserprincipal pour vérifier que l'utilisateur est authentifié (non null si authentifié)
   - isUserInRole pour vérifier que l'utilisateur a un droit donné
   - isSecure pour vérifier que la connexion réseau est sécurisée








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Basé sur le container : cas de Tomcat
- Interface Authenticator : valve qui récupère des informations d'authentification
- Interface realm : validation des credentials et affectation des rôles
- Se définissent dans un war META-INF/context.xml ou directement sur Tomcat dans /conf/service-name/host-name/appli-name.xml
- En pratique dans META-INF/context.xml en local, dans /conf/service-name/host-name/appli-name.xml sur une plateforme CEI






<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Basé sur le container : cas de Tomcat
- L'altération de la requête se fait plus tôt
- Une valve fonctionne comme un filtre mais elle s'éxécute en premier, en particulier avant la vérification des security-constraints
- Cela permet d'utiliser les security-constraints pour donner des droits à des types d'utilisateurs
- La valve se définit dans le context.xml (/META-INF/context.xml). Dans notre cas c'est un Authenticator : il possède une méthode authenticate appelée par invoke.
- Il faut également définir un realm qui permet de définir la base de données des utilisateurs (devant implémenter l'interface Realm)
```xml
<Context>
	<Valve className="package.to.tomcat.saml.sp.SPAuthenticator"
		oiosamlHome="src/test/resources/config" /> <!-- Classe d'appel implémentant (via classes abstraites) Valve et sa méthode invoke et paramètres -->
	<Realm className="package.to..tomcat.saml.sp.SAMLRealm"
		roleAttribute="roles" usernameAttribute="uid" /> <!--  Classe implémentant le realm est ses attributs -->
</Context>
```









<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Par défaut le realm de Tomcat est le UserDatabaseRealm configuré pour lire les utilisateurs définis dans conf/tomcat-users
```xml
<tomcat-users>
  <user name="tomcat" password="tomcat" roles="tomcat" />
  <user name="role1" password="tomcat" roles="role1" />
  <user name="both" password="tomcat" roles="tomcat,role1" />
</tomcat-users>
```

Un realm très simple consiste à rechercher dans un annuaire un contact et à supposer par exemple que le mot de passe est l'identifiant :
```xml
<Realm className="org.apache.catalina.realm.JNDIRealm"
		connectionURL="ldap://mon.ldap:389" userBase="ou=Personnes,o=org,c=fr"
		userSearch="(uid={0})" userPassword="uid"
		userRoleName="attributHabilitation" />
```







<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Il n'y a pas qu'une combinaison possible de Valve/Realm
- On peut s'authentifier par formulaire et vérifier les credentials de l'utilisateur sur un annuaire LDAP
- On peut autoriser une authentification basique et vérifier également les credentials sur un annuaire LDAP
- Un seul authenticator possible
- Possibilités de plusieurs realm via "CombinedRealm" ou "LockOutRealm", ce dernier bloque les tentatives d'authentification en échec successives pour un utilisateur donné (détection de brute force).








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Définition des droits avec security-constraints
```xml
<security-constraint>
  <display-name>Zone privée</display-name><!-- Pour les logs -->
  <web-resource-collection> <!-- Définition des urls et méthodes où la contrainte va s'appliquer -->
    <web-resource-name>private</web-resource-name>
    <url-pattern>/private/*</url-pattern>
    <http-method>GET</http-method>
  </web-resource-collection>
  <auth-constraint> <!-- Rôles autorisés. Mettre une étoile (*) si on veut seulement un utilisateur possédant un rôle quel qu'il soit -->
    <role-name>utilisateurs-ssm</role-name>
  </auth-constraint>
  <user-data-constraint><!-- Cumulable avec contrainte HTTPS -->
    <transport-guarantee>CONFIDENTIAL</transport-guarantee>
  </user-data-constraint>
</security-constraint>

<!-- Les rôles utilisés doivent être définis à part -->
<security-role>
		<role-name>utilisateurs-ssm</role-name>
	</security-role>
```









<!-- .slide: class="slide" -->
### Tomcat et filters
- Attention : les "security-constraints" sont vérifiées AVANT les "filters"
- Dans l'ordre une requête est modifiée par les valves, puis validée les security-constraints, puis modifiée par les filters







<!-- .slide: class="slide" -->
### Implémentation de l'authentification
- Les authentifications les plus simples peuvent être définies dans le web.xml (Tomcat va affecter automatiquement la valve (Authenticator) qui va bien)
```xml
<login-config>
  <auth-method>FORM</auth-method>
  <form-login-config>
    <form-login-page>/login.html</form-login-page>
    <form-error-page>/login-failed.html</form-error-page>
  </form-login-config>
</login-config>
```
```xml
<login-config>
		<auth-method>BASIC</auth-method>
    <realm-name>Nom d'affichage realm</realm-name>
</login-config>
```
Mais aussi CLIENT-CERT et DIGEST (digest = basic mais mdp haché avec un sel fourni par le serveur)









<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Pour l'utilisation des formulaires dans la plupart des implémentations "simples", il est attendu que le formulaire ait les champs :
- j_username contenant l'identifiant de l'utilisateur
- j_password contenant son mot de passe

Le formulaire doit appeler l'action "j_security_check"







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
    <version>5.0.7.RELEASE</version>
    <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.0.7.RELEASE</version>
</dependency>
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
- Définition page erreur 403 : ```.exceptionHandling().accessDeniedPage("/accessDenied")```
- Vérifier le HTTPS : ```.requiresChannel().antMatchers("/**").requiresSecure()```
- Vérifier les droits : ```authorizeRequests().antMatchers(/public/**).permitAll().antMatchers(/private/**).authenticated().antMatchers("/admin/**").hasRole("admin")```
  - permitAll() = accès public
  - authenticated() = utilisateurs authentifié sans controle de role
  - hasRole("admin") = utilisateurs authentifiés avec le rôle "admin"








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
- Attention dans Spring security, tous les rôles doivent s'appeler ROLE_monroleapplicatif
- Les rôles ne commençant pas par ROLE_ ne seront pas pris en compte
- Dans ce cas lors d'un "hasRole()", il faut recherche uniquement "monroleapplicatif" et non pas "ROLE_monroleapplicatif"
- En revanche les Authorities acceptent tout et on appelle les rôles directement
- Intérêt de role ? 
  - Spring Security n'ajoutera que les ROLE_* dans les role de l'objet HttpRequest
  - Donc les méthodes l'utilisant (isUserInRole par exemple) ne marcheront que dans ces conditions







<!-- .slide: class="slide" -->
### Implémentation de l'authentification
- Exemple : Ajout dans Spring Security des rôles "ROLE_utilisateur" et "user"
- L'utilisateur A possède le rôle "ROLE_utilisateur" et l'utilisateur B le rôle "user"
- `.antMatchers("/private/**").hasAuthority("utilisateur")` autorisera l'accès à aucun de ces utilisateurs
- `.antMatchers("/private/**").hasAuthority("ROLE_utilisateur")` autorisera l'accès à l'utilisateur A
- `.antMatchers("/private/**").hasAuthority("user")` autorisera l'accès à l'utilisateur B
- `.antMatchers("/private/**").hasRole("utilisateur")` autorisera l'accès à l'utilisateur A
- `.antMatchers("/private/**").hasRole("user")` autorisera l'accès à aucun de ces utilisateurs
- L'adapter Keycloak Spring Security propose un bricolage autour de cette gestion des rôles







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
	@WithMockUser(username = "Alfred Robichu", roles = { "utilisateur_oidc" })
	public void recupererPageAdminEnEtantAdmin() throws Exception {
		mvc.perform(get("/admin").secure(true)).andExpect(status().isOk())
				.andExpect(model().attribute("nom", "Alfred Robichu")).andReturn();
	}
```