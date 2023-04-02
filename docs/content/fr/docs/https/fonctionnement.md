---
title: "Fontionnement de https"
description: ""
lead: ""
draft: false
images: []
menu:
  docs:
    parent: "https"
weight: 301
toc: true
mermaid: true
---


## Les grands principes
- HTTP protocole purement applicatif "Web" : toutes données échangées sont en clair
- Protocole SSL ou TLS (le nom dépend de la version) : gestion d'un tunnel sécurisé
- Des protocoles purement fonctionnels (HTTP,FTP,SMTP,...) peuvent se sécuriser simplement en s'échangeant dans un tunnel SSL
- HTTPS = HTTP + SSL

### Proposer HTTPS sur un serveur
- Avoir un listener dédié au HTTPS (sur le port 443 dans une configuration standard) : le HTTPS est "implicite"
- Avoir une configuration HTTPS minimale, soit un couple clé privée/clé publique


### Echange sécurisé
- Les deux parties, client et serveur, doivent se mettre d'accord sur une clé symétrique
- Il existe des méthode pour communiquer sur un réseau en clair une clé symétrique secrète, qui sera en fait partiellement généré par les deux parties ([Algorithme de Diffie-Hellman](https://fr.wikipedia.org/wiki/%C3%89change_de_cl%C3%A9s_Diffie-Hellman))

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    participant C as TLS Client
    participant S as TLS Serveur
    C->>S: Client Hello
    note over C,S: Bonjour, <br/> je supporte les versions de TLS <br/> et les algorithmes suivants : <br/> ..........
    S->>C: Server Hello
    note over C,S: Bonjour, <br/> parmi les versions et algorithme que je supporte, <br/> nous allons utiliser TLS 1.2 et cet algorithme : <br/> TLS_DHE_RSA_WITH_AES_256_CBC_SHA
    note over C,S: Voici mon certificat : .......
    opt
      note over C,S: J'aurai besoin d'un certificat à ton nom <br/> pour t'authentifier
    end
    C->>C: Controle du certificat
    note over C: Nom valide ? <br/> Date valide ? <br/> AC reconnue ?
    C->>S: Préparation clé (Diffie-Hellman)
    note over C,S: chiffrée avec clé publique serveur
    S->>S: Déchiffrement avec clé privée
    note over S: Fait office de challenge response
    opt
      S->>S: Controle Certif client le cas échéant
      S->>C: Challenge
      C->>S: Challenge response   
    end
    S->>C: Finalisation Diffie-Hellman
    note over C,S: Le client et le serveur <br/> se sont partagé une clé symétrique <br/> à usage unique à cette communication
    C->>S: OK, next encrypted
    S->>C: OK, next encrypted
    loop
        C->>S: encrypted {get /resource/}
        S->>C: encrypted {reponse HTTP}
    end
{{< /mermaid >}}


La clé symétrique est jetable, non stockée en dehors de cette échange, elle ne peut donc pas être compromise par la suite.
On parle de confidentialité persistente ou PFS (Perfect Forward Secrecy)

### Controle d'identité et autorité de confiance

- A quoi sert la clé asymétrique alors ?
- Problème du "Man in the middle"
  - "Information invisible par un tiers"...
  - Encore faut il maîtriser les deux parties
  - Le client doit savoir qui il est en train de contacter
  - Le serveur propose un certificat contenant principalement
  - son nom : l'url que l'utilisateur a demandé
  - sa clé publique
  - la signature de ces informations par une autorité de confiance


**L'autorité de confiance ?**
 - C'est un organisme qui signe avec sa clé privée les informations du serveur
 - On peut donc vérifier avec la clé publique de l'AC si informations et signature correspondent
 - Pour récupérer la clé publique de l'Autorité de confiance, on consulte son...
 - Certificat ! ... Mais comment vérifier son certificat ?
 - Le certificat d'une AC est autosignée, le navigateur est configuré pour accepter un certain nombre
 - L'insee est une AC reconnue en interne uniquement
 - Les applis Internet sont signées par une AC externe reconnue dans le monde entier (intégrée sur les navigateurs connus)

## Déléguer HTTPS

- L'application ne gère pas le HTTPS, c'est le serveur applicatif (tomcat) ou un équipement réseau supérieur qui en a la charge
- L'application peut imposer l'utilisation du HTTPS

- Dans tous les cas c'est le serveur applicatif (tomcat) qui a la charge de déterminer si la connexion entrante est sécurisée
- Cas direct : c'est tomcat qui gère lui même la connexion ssl
- Cas indirect : tomcat ne gère pas de connexion HTTPS mais il sait que la connexion initiale est en HTTPS (cas d'un reverse proxy)
- Si le connecteur gère la connexion ssl ou qu'il est dédié à recevoir des connexions sécurisées, on peut directement le spécifier sécurisé.
- Sinon, il va falloir définir un profil de requêtes sécurisées

### Répartiteur de charge déchiffrant
- Architecture Load Balancer qui porte les certificats (et donc gère la connexion HTTPS)
- Le load balancer déchiffre et transmet une requête HTTP (en clair) au serveur applicatif portant l'application

**Transfert de l'information HTTPS**
- Pour savoir que la requête initiale est en HTTPS, le load balancer ajoute un entête "X-Forwarded-Proto: https"

- On va ensuite configurer notre serveur applicatif (tomcat) pour que la lecture de cet entête signifie "La connexion initiale était sécurisé"

```xml
<Valve className="org.apache.catalina.connector.RemoteIpValve" protocolHeader="X-Forwarded-Proto" />
```

> Une valve Tomcat est une classe exécutée (invoke()) à l'arrivée de chaque requête. C'est le même fonctionnement que les filtre JavaEE/JakartaEE


## Vraiment forcer le HTTPS
- Niveau applicatif pour controle éventuellement
- Niveau Frontal (Load Balancer) : écouter en http sur le port 80 mais uniquement pour rediriger vers https
- **Déclaratif** Indiquer au navigateur de ne plsu utiliser que HTTPS : en-tête HSTS
```
Strict-Transport-Security "max-age=31536000"
```

La configuration ne sera activée par le navigateur que s'il y a eu une première fois une commication sécurisée correctement ! 
=> Validation ok du certificat 


## Le truststore

On va spécifier un peu plus l'étape "Vérification du certificat serveur"

- Mon application doit se connecter à une autre application (web-service)
- Je souhaite que cet appel soit en https (au fond justifié que si la connexion entre les deux serveurs passent par Internet)
- Lors d'une connexion HTTPS, Java vérifie si le certificat distant est valide
- C'est ce que fait le navigateur quand on se connecte à un site
- Si le certificat n'est pas valide (expiré, non associé au domaine, non signé par une autorité de confiance), le navigateur me le signale et je choisit de prendre le risque ou non de me connecter au site.
- Dans l'appli, Java refusera systématiquement la connexion


- Date d'expiration, mauvais nom : je ne peux rien faire, il faut que le certificat soit cohérent
- Autosigné ? Je peux dire à Java que le signataire du certificat est valide, en d'autres termes, je lui définis une autorité de confiance
- (Il existe la possibilité de créer une classe faisant que Java ne fait plus aucun contrôle sur le certificat mais c'est mal)
- Il faut créer un truststore contenant l'ensemble des certificats à valider (= ce que l'on considère AC)



## Https sur une application javascript
- On peut toujours déléguer le https à un équipement frontal réseau
- A noter que l'échange sécurisé permet donc ici de garantir l'échange correcte du **code source**, il est donc indispensable que l'utilisateur soit redirigé au plus tôt vers https

On peut éventuellement forcer le https au sein de l'application, même si c'est un peu tard...
- L'application s'éxécute chez le client, pour forcer le https, il faut donc raisonner côté client
- Si l'application JS veut s'assurer de passer en HTTPS, elle doit vérifier le window.location du client et éventuellement le rediriger.

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

[Pour lancer une application "create-react-app" en https en local (par defaut http)](https://facebook.github.io/create-react-app/docs/using-https-in-development) : 
- Au lieu de `npm start`
- Lancer `set HTTPS=true&&npm start`


*Https sur les application javascript : cas du back-end mal signé*
Un front-end JavaScript effectue des appels à son backend.

Dans la pratique, c'est le navigateur qui effectue ces requetes, et si le certificat du backend ne convient pas au navigateur, la requete va être rejetée.

- Solution 1 : Faire en sort que le backend ait un certificat signé par l'Insee
- Solution 2 : se connecter manuellement une fois au backend, accepter l'alerte de sécurité du navigateur
