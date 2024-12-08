---
title : "L'authentification sur tomcat"
description: ""
lead: ""
draft: false
images: []
weight: 605
mermaid: true
---

### Les grands principes

Deux interfaces :

- `Authenticator` : interaction avec l'utilisateur pour récupérer des informations d'authentification (formulaire, basic, certificat, ...)
- `Realm` : validation des credentials fournis et récupération d'information utilisateur notamment les rôles (ldap, fichier local, ...)

La configuration des ces élements se fait via un fichier de contexte déposé sur :
- en interne au livrable : `META-INF/contexte.xml`
- en surcharge : `${CATALINA_BASE}/conf/<service-name>/<host-name>/<appli-name>.xml` soit dans un cas standard sur une application déployée à la racine `${CATALINA_BASE}/conf/Catalina/localhost/ROOT.xml`

### Authenticator

Des `Authenticator` existant par défaut. Pour ces Authentcator, un conf simplifiée permet de les activer via la balise `login-config`
- `BASIC` pour activer `BasicAuthenticator`
- `FORM` pour activer `FormAuthenticator`
- `CLIENT_CERT` pour activer `SSLAuthenticator`
- `NONE` active `NonLoginAuthenticator` qui permet de forcer la lecture des security-constraint sans authentification

### Realm

Les `Realm`offerts par défaut dans tomcat sont notamment :
- `JNDIrealm` permettant de s'authentifier sur un annuaire LDAP et de faire ensuite une requete de profil utilisateur et une requete de récupération de droit
- `DataSourceRealm` depuis une source JDBC
- `UserDatabaseRealm` utilise le fichier local `tomcat-users.xml` comme source d'utilisateurs

Il existe également des realms permettant la combinaison et l'extension des realms "unitaires" :
- `CombinedRealm` permet de juxtaposer plusieurs realms permttant la recherche d'utilisateur dans chaque sous-realm
- `LockoutRealm` est un CombinedRealm qui permet en plus la gestion de la brute force (détection d'une sucession d'échecs avec un username donné)
