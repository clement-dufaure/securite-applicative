# Sécurité applicative

:arrow_forward: [Diaporama](https://formation.dufau.re/securite-applicative)


## 0. Récupérer le code source du TP

### 0.1. Cloner le dépot git

```bash
git clone https://github.com/clement-dufaure/securite-applicative.git
```

### 0.2. Présentation du contenu

Les exercices s'effectuent sur java/application-basique-todo, puis js/application-basique-js-todo.

Le module "java/application-basique-todo" est une application spring mvc très basique.

Le module "js/application-basique-js-todo" contient un modèle basique de page html pour insérer de code javascript.

Le module "application-basique-react" et "application-basique-react-axa" contiennent des démos React/Recoil avec respectivement les adapter Keycloak et Axa.

Les autres projets sont des démo de chaque type d'authentification selon plusieurs implémentations. (correction des exercices)

### 0.3. Autres prérequis

Une distribution tomcat : https://tomcat.apache.org/download-10.cgi

> Pour Windows, télécharger la version zip et non le msi

## 1. Mise en place de HTTPS

### 1.1 Générer un keystore pour une configuration https de localhost
Ligne de commande : git bash par exemple

### 1.2 Mettre en place https sur un serveur local
Utiliser le keystore précedemment créé pour ajouter une configuration HTTPS sur un tomcat.

Les configurations doivent directement s'écrire dans le server.xml (pas d'interface graphique paramétrable dans éclipse).
Dans Eclipse, un projet "Servers" contient l'ensemble des configurations des serveurs créés.

### 1.3 Forcer le https sur l'application
Rester dans le cas de Tomcat pour le moment : manipuler le web.xml *de l'application*

### 1.4 (Facultatif) Créer un truststore incluant le cacert Insee et le certificat généré précédemment, le faire prendre en compte dans l'application
Il faut récupérer le trustore par défaut (dans l'installation java de l'atelier de dev) et ajouter le certificat localhost.
Pour l'intégrer dans l'application, on pourra ici simplement le déclarer dans les arguments de démarrage de tomcat.

## 2. Monter localement un serveur Keycloak

- Télécharger la dernière version de keycloak sur https://www.keycloak.org/downloads , version Quarkus en zip
- Keycloak démarre simplement via `bin/kc.sh start-dev` ou `bin/kc.bat start-dev`
- Par défaut il démarre sur le port 8080, ce qui peut être génant selon vos habitudes de développement, pour le changer et le faire démarre par exemple sur 8180 : `bin/kc.sh start-dev --http-port=8180`
- préparer un realm, un client, et un utilisateur de test

## 3. Savoir gérer une authentification avec un fournisseur d'identité

### 3.1 S'authentifier avec le filtre Keycloak OIDC
À partir du projet application-basique-todo, ajouter le filtre Keycloak pour que la page /private  ne soit accessible que pour un utlisateur authentifié.

Afficher quelques éléments du jeton. Récupérer les informations depuis le web service /userifo (connexion avec jeton).

Eventuellement se connecter à un web service local (par exemple application-basique-ws-oidc). Le truststore doit contenir le certificat localhost.

### 3.2 Gérer les droits des utilisateurs
Controler en plus que les utilisateurs se connectant à /admin aient le droit utilisateur_oidc

### 3.3 S'authentifier avec l'adapter Spring Security Keycloak
Refaire les deux exercices précédent en utilisant Spring Security

### 3.4 S'authentifier avec l'adapter Keycloak JavaScript
Compléter la page HTML pour qu l'utilisateur soit authentifié autoatiquement ou via un bouton login.

Récupérer des informations du jeton et effectuer une connexion sur /userinfo

### 3.5 Utiliser Keycloak avec React/Redux
Compléter le composant Login afin de gérerl'authentification et placement dans le store.

Vérifier l'authentification pour accéder au composant Token, afficher certaines info du jeton, et se connecter au webService /userinfo en utilisant axios (configuration spéciale définie dans AxiosToken.js)

## 4 Compléments

### 4.1 Développer un thème pour son realm
Rajouter un thème personnalisé sur le serveur keycloak local


