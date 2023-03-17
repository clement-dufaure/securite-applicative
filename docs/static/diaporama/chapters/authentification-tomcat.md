<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 3.2
### Authentification avec Tomcat








<!-- .slide: class="slide" -->
### Implémentation de l'authentification
Basé sur le container : cas de Tomcat
- Interface Authenticator : valve qui récupère des informations d'authentification
- Interface realm : validation des credentials et affectation des rôles
- Se définissent dans un war META-INF/context.xml
- Sont surchargé dans /conf/Catalina/localhost/appli-name.xml (c'est le cas sur une plateforme CEI)






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
	<Valve className="package.to.Authenticator"/> <!-- Classe d'appel implémentant (via classes abstraites) Valve et sa méthode invoke -->
	<Realm className="package.to.Realm"/> <!--  Classe implémentant le realm -->
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

