---
title: "Mise en pratique de https"
description: ""
lead: ""
draft: false
images: []
menu:
  docs:
    parent: "https"
weight: 302
toc: true
---

## Générer un certificat

Les étapes :
- Générer une clé privée
- Calculer la clé publique à partir de la clé privée
- Enrober la clé publique dans une demande de certificat
- Signer la demande de certificat
- (facultatif) Générer le bon format d'échange


*Qui signe ?*
- Une autorité payante
- Let's encrypt
- Nous-même dans le cadre de l'exercice ! Certifcat dit autosigné, ici généré pour localhost, signé par localhost

```bash
# On génère une clé privée
openssl genrsa 2048 > server.key
```

```bash
# On génère la clé publique associé à la clé privée, et on prépare la demande de certificat
openssl req -new -key server.key -out server.csr
```

```
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
```

```bash
# On fait signer la demande par l'AC qui ici est localhost lui-même
openssl x509 -req -days 365 -in server.csr  -signkey server.key  -out server.crt
```

```bash
# Si on veut transformer en keystore
openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -passout pass:changeit
```

## Installation sur tomcat

Configuration des ports d'écoute : dans le server.xml :

### Ajout d'un connecteur SSL

```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
			maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
			clientAuth="false" sslProtocol="TLS" keystoreFile="${catalina.home}/conf/ssl/server.p12" keystoreType="pkcs12"
			keystorePass="changeit" />
```

### Configuration de la redirection du connecteur par défaut

On définie le port de redirection dans le cas ou une redirection https est imposée par l'application

**Ce connecteur existe déjà par défaut, il faut uniquement changer le port de redirection si nécessaire**

```xml
<Connector connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>
```

### Rendre le https obligatoire

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

*Spring Security*
```java
@Configuration
@EnableWebSecurity
public class MySecurityConfiguration  {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.requiresChannel().anyRequest().requiresSecure();
  }
}
```


## TP
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
