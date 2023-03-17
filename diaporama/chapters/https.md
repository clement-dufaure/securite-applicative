<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 2
### Mise en place de HTTPS

-----

<!-- .slide: class="slide" -->
### Les grands principes
HTTPS intervient dans :
- Intégrité
- Confidentialité
- Authentification

-----

<!-- .slide: class="slide" -->
### Les grands principes
- HTTP protocole purement applicatif "Web" : toutes données échangées sont en clair
- Protocole SSL ou TLS (le nom dépend de la version) : gestion d'un tunnel sécurisé
- Des protocoles purement fonctionnels (HTTP,FTP,SMTP,...) peuvent se sécuriser simplement en s'échangeant dans un tunnel SSL
- HTTPS = HTTP + SSL

-----

<!-- .slide: class="slide" -->
### Proposer HTTPS
- Avoir un listener dédié au HTTPS (sur le port 443 dans une configuration standard) : le HTTPS est "implicite"
- Avoir une configuration HTTPS minimale, soit un couple clé privée/clé publique

-----

<!-- .slide: class="slide" -->
### Echange sécurisé
- Les deux parties, client et serveur, doivent se mettre d'accord sur une clé symétrique
- Il existe des méthode pour communiquer sur un réseau en clair une clé symétrique secrète, qui sera en fait partiellement généré par les deux parties ([Algorithme de Diffie-Hellman](https://fr.wikipedia.org/wiki/%C3%89change_de_cl%C3%A9s_Diffie-Hellman))
- A quoi sert la clé asymétrique alors ?
- Problème du "Man in the middle" <!-- .element: class="fragment"-->

-----

<!-- .slide: class="slide" -->
### Confidentialité
 - "Information invisible par un tiers"...
 - Encore faut il maîtriser les deux parties
 - Le client doit savoir qui il est en train de contacter
 - Le serveur propose un certificat contenant principalement
  - son nom : l'url que l'utilisateur a demandé
  - sa clé publique
  - la signature de ces informations par une autorité de confiance

-----

 <!-- .slide: class="slide" -->
### Autorité de confiance ?
 - C'est un organisme qui signe avec sa clé privée les informations du serveur
 - On peut donc vérifier avec la clé publique de l'AC si informations et signature correspondent
 - Pour récupérer la clé publique de l'Autorité de confiance, on consulte son...
 - Certificat ! ... Mais comment vérifier son certificat ?
 - Le certificat d'une AC est autosignée, le navigateur est configuré pour accepter un certain nombre
 - L'insee est une AC reconnue en interne uniquement
 - Les applis Internet sont signées par une AC externe reconnue dans le monde entier (intégrée sur les navigateurs connus)

-----

 <!-- .slide: class="slide" -->
### La poignée de main, ou handshaking
- Le client fait une demande de transaction sécurisée au serveur
- Le serveur renvoie son certificat, contenant la clé publique
- Le client valide le certificat
- Le client génère une clé secrète qu'il chiffre avec la clé publique. La clé secrète est envoyée au serveur
- Le serveur déchiffre la clé secrète
- Cette clé est utilisée pour chiffrer les prochains échanges entre le client et le serveur

-----

 <!-- .slide: class="slide" -->
### La poignée de main, ou handshaking
<img src="./images/handshake.gif" />

-----

 <!-- .slide: class="slide" -->
### Comment faire en local ?
- Il n'est pas nécessaire d'avoir un certificat signé par une autorité de confiance
- Un certificat autosigné suffit : c'est un certificat "localhost" signé par "localhost"

-----

 <!-- .slide: class="slide" -->
### Génération d'un certificat autosigné
 ```shell
# On génère une clé privée
openssl genrsa 2048 > server.key

# On génère la clé publique associé à la clé privée, et on prépare la demande de certificat
openssl req -new -key server.key -out server.csr
> Country Name (2 letter code) [AU]:FR
> State or Province Name (full name) [Some-State]:France
> Locality Name (eg, city) []:Paris
> Organization Name (eg, company) [Internet Widgits Pty Ltd]:Insee
> Organizational Unit Name (eg, section) []:CNIP
> Common Name (e.g. server FQDN or YOUR name) []:localhost
> Email Address []:

> Please enter the following 'extra' attributes
> to be sent with your certificate request
> A challenge password []:
> An optional company name []:

#On fait signer la demande par l'AC qui ici est localhost lui-même
openssl x509 -req -days 365 -in server.csr  -signkey server.key  -out server.crt

#Si on veut transformer en keystore
openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -passout pass:changeit
 ```

-----

 <!-- .slide: class="slide" -->
### HTTPS en pratique
- L'application ne gère pas le HTTPS, c'est le serveur applicatif (tomcat) ou un équipement réseau supérieur qui en a la charge
- L'application peut imposer l'utilisation du HTTPS

-----

<!-- .slide: class="slide" -->
### HTTPS en pratique
- Dans tous les cas c'est le serveur applicatif (tomcat) qui a la charge de déterminer si la connexion entrante est sécurisée
- Cas direct : c'est tomcat qui gère lui même la connexion ssl
- Cas indirect : tomcat ne gère pas de connexion HTTPS mais il sait que la connexion initiale est en HTTPS (cas d'un reverse proxy)
- Si le connecteur gère la connexion ssl ou qu'il est dédié à recevoir des connexions sécurisées, on peut directement le spécifier sécurisé.
- Sinon, il va falloir définir un profil de requêtes sécurisées

-----

<!-- .slide: class="slide" -->
### HTTPS indirect en pratique : en production
- Architecture Load Balancer qui porte les certificats (et donc gère la connexion HTTPS)
- Le load balancer déchiffre et transmet une requête HTTP (en clair) au serveur applicatif portant l'application

-----

<!-- .slide: class="slide" -->
### HTTPS indirect en pratique : en production
Transfert de l'information HTTPS
- Pour savoir que la requête initiale est en HTTPS, le load balancer ajoute un entête "X-Forwarded-Proto: HTTPS", et on configure Tomcat pour que la lecture de cet entête signifie sécurisé

```xml
<Valve className="org.apache.catalina.connector.RemoteIpValve" protocolHeader="X-Forwarded-Proto" />
```

Une valve Tomcat est une classe exécutée (invoke()) à l'arrivée de chaque requête.

-----

<!-- .slide: class="slide" -->
### HTTPS direct en pratique : sur localhost
*Sur Tomcat*

Dans server.xml :

- Ajout d'un connecteur SSL

```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
			maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
			clientAuth="false" sslProtocol="TLS" keystoreFile="${catalina.home}/conf/ssl/server.p12" keystoreType="pkcs12"
			keystorePass="changeit" />
```

-----

<!-- .slide: class="slide" -->
### HTTPS direct en pratique
*Sur Tomcat*

Dans server.xml

- Définition du port de redirection dans le cas d'un connecteur non sécurisé

```xml
<Connector connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>
```

-----

<!-- .slide: class="slide" -->
### Sur l'appli, pour indiquer qu'on impose du https
*web.xml* (Tomcat)
```xml
<security-constraint>
  <display-name>tout-en-https</display-name>
  <web-resource-collection>
    <web-resource-name>tout</web-resource-name>
    <url-pattern>/*</url-pattern>
  </web-resource-collection>
  <user-data-constraint>
    <transport-guarantee>CONFIDENTIAL</transport-guarantee>
  </user-data-constraint>
</security-constraint>
```

-----

<!-- .slide: class="slide" -->
### Sur l'appli, pour indiquer qu'on impose du https
*Spring Security*
```java
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Override
  	protected void configure(HttpSecurity http) throws Exception {
      http.requiresChannel().antMatchers("/**").requiresSecure();
    }

}
```

-----

<!-- .slide: class="slide" -->
### TP
- Générer un keystore pour une configuration https de localhost
- Mettre en place https sur un serveur local
- Forcer le https sur l'application

-----

<!-- .slide: class="slide" -->
### Vraiment forcer le HTTPS
- Niveau applicatif pour controle éventuellement
- Niveau Frontal (Load Balancer) : écouter en http sur le port 80 mais uniquement pour rediriger vers https
- **Déclaratif** Indiquer au navigateur de ne plsu utiliser que HTTPS : en-tête HSTS
```
Strict-Transport-Security "max-age=31536000"
```

-----
<!-- .slide: class="slide" -->
### Le truststore
- Mon application doit se connecter à une autre application (web-service)
- Je souhaite que cet appel soit en https (au fond justifié que si la connexion entre les deux serveurs passent par Internet)
- Lors d'une connexion HTTPS, Java vérifie si le certificat distant est valide
- C'est ce que fait le navigateur quand on se connecte à un site
- Si le certificat n'est pas valide (expiré, non associé au domaine, non signé par une autorité de confiance), le navigateur me le signale et je choisit de prendre le risque ou non de me connecter au site.
- Dans l'appli, Java refusera systématiquement la connexion

-----

<!-- .slide: class="slide" -->
### Le truststore
- Date d'expiration, mauvais nom : je ne peux rien faire, il faut que le certificat soit cohérent
- Autosigné ? Je peux dire à Java que le signataire du certificat est valide, en d'autres termes, je lui définis une autorité de confiance
- (Il existe la possibilité de créer une classe faisant que Java ne fait plus aucun contrôle sur le certificat mais c'est mal)
- Il faut créer un truststore contenant l'ensemble des certificats à valider (= ce que l'on considère AC)

-----

<!-- .slide: class="slide" -->
### Le truststore
- Par défaut le trustore chargé est celui de la jvm éxécutant l'application (....\\mon-jdk\\lib\\security\\cacerts)
- Le mot de passe par défaut est changeit
- On peut vouloir un "Insee flavored cacert" : le cacerts disponible dans la distribution java plus la chaine de certification Insee (AC Racine + AC Subordonnée)
- Au cas où : [certificats AC Insee](http://crl.insee.fr/)

-----

<!-- .slide: class="slide" -->
### Le truststore
- Pour le changer on utilise des paramètres de la JVM :
  - javax.net.ssl.trustStrore
  - javax.net.ssl.trustStorePassword
  - javax.net.ssl.trustStoreType (par défaut jks)
- On crée un nouveau truststore, en pratique on ajoute nos certificats au truststore par défaut

-----

<!-- .slide: class="slide" -->
### Le truststore
- Créer le truststore

```
keytool  -import -trustcacerts -file server.crt -alias localhost -keystore cacertsperso
```

Si keytool inconnu : (à ajouter au path pour plus de simplicité)
```
"path/to/mon-jdk/bin/keytool.exe"
"$JAVA_HOME/bin/keytool.exe"
```

Crée le truststore si cacertsperso n'existe pas encore, ajoute le certificat aux existants s'il existe déjà

-----

<!-- .slide: class="slide" -->
### Le truststore
- Le déclarer au démarrage :
  - Paramètres de démarrage

```
-Djavax.net.ssl.trustStore="/path/to/cacerts.jks" -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=JKS
-Djavax.net.debug=all (UNIQUEMENT pour debugger en cas de problème, c'est TRES verbeux)
```
  - Dans le code :

```java
System.setProperty("javax.net.ssl.trustStore", "/path/to/cacerts.jks");
System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
System.setProperty("javax.net.ssl.trustStoreType", "JKS");
```

-----

<!-- .slide: class="slide" -->
### TP
- Créer un truststore incluant le cacert Insee et le certificat généré précédemment
- Le faire prendre en compte dans l'application

-----

<!-- .slide: class="slide" -->
### Https sur les application javascript
- Fonctionnement proche : réalisé par le F5 en prod, par l'apache en dv/qf (mais il n'y a pas de tomcat)
- L'application s'éxécute chez le client, pour forcer le https, il faut donc raisonner côté client
- Si l'application JS veut s'assurer de passer en HTTPS, elle doit vérifier le window.location du client et éventuellement le rediriger.

-----

<!-- .slide: class="slide" -->
### Https sur les application javascript
```js
if (
   typeof window !== 'undefined' &&
   window.location &&
   window.location.protocol === 'http:'
 ) {
   window.location.href = window.location.href.replace(
     /^http(?!s)/,
     'https'
   );
 }
```

-----

<!-- .slide: class="slide" -->
### Https sur les application javascript : développement
[Pour lancer une application "create-react-app" en https en local (par defaut http)](https://facebook.github.io/create-react-app/docs/using-https-in-development) : 
- Au lieu de `npm start`
- Lancer `set HTTPS=true&&npm start`

-----

<!-- .slide: class="slide" -->
### Https sur les application javascript : cas du back-end mal signé
Un front-end JavaScript effectue des appels à son backend.

Dans la pratique, c'est le navigateur qui effectue ces requetes, et si le certificat du backend ne convient pas au navigateur, la requete va être rejetée.

- Solution 1 : Faire en sort que le backend ait un certificat signé par l'Insee
- Solution 2 : se connecter manuellement une fois au backend, accepter l'alerte de sécurité du navigateur
