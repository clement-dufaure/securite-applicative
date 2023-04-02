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

Comment faire en local
- Il n'est pas nécessaire d'avoir un certificat signé par une autorité de confiance
- Un certificat autosigné suffit : c'est un certificat "localhost" signé par "localhost"

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

- tomcat <=9
```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
			maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
			clientAuth="false" sslProtocol="TLS" keystoreFile="${catalina.home}/conf/ssl/server.p12" keystoreType="pkcs12"
			keystorePass="changeit" />
```

- tomcat >=10
```xml
	<Connector port="8443" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
               maxThreads="20" SSLEnabled="true" scheme="https" secure="true" defaultSSLHostConfigName="test">
        <SSLHostConfig hostName="test" protocols="TLSv1.2" >
            <Certificate
                certificateFile="${catalina.home}/conf/ssl/server.crt"
                certificateKeyFile="${catalina.home}/conf/ssl/server.key"
            />
        </SSLHostConfig>
    </Connector>
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


### TP
- Générer un keystore pour une configuration https de localhost
- Mettre en place https sur un serveur local
- Forcer le https sur l'application


## Le truststore en java

- Par défaut le trustore chargé est celui de la jvm éxécutant l'application (....\\mon-jdk\\lib\\security\\cacerts)
- Le mot de passe par défaut est changeit
- On peut vouloir un "Insee flavored cacert" : le cacerts disponible dans la distribution java plus la chaine de certification Insee (AC Racine + AC Subordonnée)
- Au cas où : [certificats AC Insee](http://crl.insee.fr/)

- Pour le changer on utilise des paramètres de la JVM :
  - javax.net.ssl.trustStrore
  - javax.net.ssl.trustStorePassword
  - javax.net.ssl.trustStoreType (par défaut jks)
- On crée un nouveau truststore, en pratique on ajoute nos certificats au truststore par défaut

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


### TP
- Créer un truststore incluant le cacert Insee et le certificat généré précédemment
- Le faire prendre en compte dans l'application

