---
title : "Fonctionnalités spécifiques d'un idp : exemple de Keycloak"
description: ""
lead: ""
draft: false
images: []
weight: 613
menu:
  docs:
    parent: "oidc"
mermaid: true
---


<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 5
### Fonctionnalités Keycloak







<!-- .slide: class="slide" -->
### Keycloak
- Keycloak : gestion de royaumes distincts : chacun ayant ses utilisateurs, son thème









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
- Ou partir d'un thème insee du projet keycloak thèmes
- Actuellement thèmes Insee proposés :
  - "insee-generic" : proche de l'actuel thème de la fédération d'identité
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










<!-- .slide: class="slide" -->
### Cas particuliers










<!-- .slide: class="slide" -->
### France Connect
- Le dispositif France Connect utilise OpenIdConnect
- L'inscription à France Connect n'est pas une opération anodine
- France connect est proposé comme fournisseur d'identité pour Keycloak









<!-- .slide: class="slide" -->
### Besoin de plusieurs realms
2 cas :
- Le deuxième realm est un realm secondaire :
*une application ouverte sur internet dédié aux agents Insee doit être accessible par formulaire depuis Internet mais par Kerberos en interne : le realm agents-insee-interne et une alternative à insee-ssp*
- Les différents realms sont indépendant :
*une application transverse sur laquelle peuvent se connecter divers acteurs issus d'application différentes*







<!-- .slide: class="slide" -->
### Cas d'un realm secondaire
- Similaire à une situation s'authentifier avec Google, Facebook,...
- Un fournisseur d'identité est créé côté Keycloak : *auth.insee.fr/.../agents-insee-interne devient fourniseur d'identité pour auth.insee.net/.../insee-ssp*
- Côté application, seul le realm principal doit être déclaré
- Remarque : la déclaration de fournisseur d'identité est en OpenIDConnect si les deux realms sont sur le même serveur, en SAML s'ils sont sur interne et DMZ, une question de flux.








<!-- .slide: class="slide" -->
### Cas d'un realm secondaire
il est possible de forcer keycloak à utiliser un fournisseur de service secondaire, par exemple si un discovery service est utilisé pour savoir où se situe l'utilisateur :

Applis web :
```
GET /myapplication.com?kc_idp_hint=sso-insee
```

Applis JS :
```js
var keycloak = new Keycloak('keycloak.json');
keycloak.login({
	idpHint: 'sso-insee'
});
```









<!-- .slide: class="slide" -->
### Cas de plusieurs realms indépendants
- Le filtre Keycloak propose une interface pour founir dynamiquement une configuration keycloak
- https://www.keycloak.org/docs/latest/securing_apps/index.html#_multi_tenancy
- Déclaration d'une classe resolver de conf








<!-- .slide: class="slide" -->
### Cas de plusieurs realms indépendants
```java
public class MyKeycloakConfigResolver implements KeycloakConfigResolver {
  @Override
  public KeycloakDeployment resolve(Request facade) {
    if (facade.getCookie("realm") != null) {
      String realmDemande = facade.getCookie("realm").getValue();
      InputStream is;
      if (realmDemande.equals("keycloak1")) {
        is = getClass().getClassLoader().getResourceAsStream("keycloak1.json");
      } else {
        is = getClass().getClassLoader().getResourceAsStream("keycloak2.json");
      }
      return KeycloakDeploymentBuilder.build(is);
    } else {
      return null;
    }
  }
}
```









<!-- .slide: class="slide" -->
### Cas de plusieurs realms indépendants
```xml
	<filter>
		<filter-name>Keycloak Filter</filter-name>
		<filter-class>org.keycloak.adapters.servlet.KeycloakOIDCFilter</filter-class>
		<init-param>
			<param-name>keycloak.config.resolver</param-name>
			<param-value>fr.insee.demo.multi.tenant.MyKeycloakConfigResolver</param-value>
		</init-param>
	</filter>
  ```

