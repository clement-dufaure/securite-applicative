<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 7
### Fonctionnalités Keycloak







<!-- .slide: class="slide" -->
### Keycloak VS Shibboleth
- Shibboleth ne gère qu'un domaine : "tout le monde", problèmes d'unicité sur tous les domaines de l'Insee, problème de thèmes communs
- Keycloak : gestion de royaumes distincts : chacun ayant ses utilisateurs, son thème

>1 Keycloak = plusieurs Shibboleth









<!-- .slide: class="slide" -->
### Keycloak
Du point de vue serveur, Keycloak permet :
- La gestion d'un thème par realm et même par client
- La délégation de l'administration sur un realm

Keycloak fournit une application aux utilisateurs : l'application "Account"







<!-- .slide: class="slide" -->
### Application "Account"
Application permettant aux utilisateurs de consulter leur compte. Elle permet :
- Le changement de mot de passe
- L'activation de l'authentification forte
- La consultation des sessions en cours et des logs de connexion du compte
- La consultation des rôles









<!-- .slide: class="slide" -->
### Application "Account"
Pour rajouter le lien vers account depuis son application avec un bouton de retour à l'application :
`https://my.keycloak.server/auth/realms/my-realm/account?referrer=client-test-web&referrer_uri=https://localhost:8443/`

(il faut déjà être connécté sur le realm sinon ça fait n'importe quoi)
 






<!-- .slide: class="slide" -->
### Lancer un Keycloak local
Pour tester sans risques certaines manipulation, il est aisé de lancer un keycloak local sur son poste.
- Télécharger l'installation standalone : https://www.keycloak.org/downloads.html
- Lancer /pathKeycloak/bin/standalone.bat
- Le serveur démarre sur http://localhost:8080 et https://locahost:8443
- Sur la page d'accueil chosir "Administration console"
- Vous pouvez vraiment administrer votre serveur local, les configurations sont sauvegardées d'une utilisation à l'autre







<!-- .slide: class="slide" -->
### Gérer un realm
[Documentation sur le wiki](https://git.stable.innovation.insee.eu/outils-transverses/authentification/keycloack/keycloak-insee-ext/wikis/Administrateurs/Administration-des-Realms#gestion-dun-realm)

[Documentation officielle](https://www.keycloak.org/docs/latest/server_admin/index.html)






<!-- .slide: class="slide" -->
### Personnaliser le thème pour son realm (ou pour son application)
- Configuration globale pour le thème sur le realm
- Possibilité d'une configuration spécifique de l'écran de login pour une application cliente d'un realm

Techno
- Template + pages écrit en ftl
- Fichiers de messages, potentiellement en plusieurs langues
- Posibilité de prendre un thème standard en changeant quelques textes (comme avec Shibboleth)

Personnalisation possible pour :
- Écran de login
- Application "Account"
- email (activation, réinitialisation de mdp)





<!-- .slide: class="slide" -->
### Tester le thème
- Ajouter des thèmes dans /pathKeycloak/themes et le parmétrer dans la conf realm
- Vous pouvez vous inspirez du thème keycloak présent de base
- Ou partir d'un thème insee du projet [git@git.stable.innovation.insee.eu:22222]:outils-transverses/authentification/keycloack/keycloak-insee-ext.git
- Actuellement thèmes Insee proposés :
  - "insee-genric" : proche de l'actuel thème de la fédération d'identité
  - "insee-minimal" : thème Insee avec le moins de changement possible par rapport au thème keycloak






<!-- .slide: class="slide" -->
### Possibilités de Keycloak sur les rôles
Souhaitable
- Gestion de scope de rôles : on ne fournit à l'application cliente que les rôles dont elle a besoin.

Possibilités "manuelles"
- Roles déduits : on peut préciser que le rôle d'administrateur implique celui d'utilisateur. Cela permet de ne spécifier que le rôle d'admin sur l'annuaire
- Roles composites : combinaisons de plusieurs rôles
- Roles déduits d'une autentification en particulier (cas de redirection vers fournisseur interne)

Existe mais peu compatible avec notre archtecture
- Roles spécifiques à un client, (par défaut propre au realm).
