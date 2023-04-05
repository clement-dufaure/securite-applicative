---
title : "Les grands principes de l'authentification"
description: ""
lead: ""
draft: false
images: []
weight: 600
---

## Qu'est qu'une personne authentifiée
- Une personne connue de l'application
- Elle est donc enregistrée
- Comment stocker cette information ? Type de base de donnée dédiée : l'annuaire

## Qu'est que l'authentification
- Validation d'une identification
- Un utilisateur doit indiquer qui il est (identifiant, certificat client) et le prouver (mot de passe, certifcat client, carte à puce, code à usage unique, empreinte digitale)
- Au moins un facteur, mais plusieurs facteurs simultanés améliorent la preuve (mot de passe + carte à puce)

## Le SSO - Single Sign On
- S'identifier une seule fois pour accéder à plusieurs services
- Système d'authentification centralisé
- Question du SLO
  - Logout = déconnexion de l'application uniquement ?
  - Ou déconnexion de l'application et du fournisseur d'identité ?
  - Ou encore, de l'application, du fournisseur, et de toutes les autres applis s'étant connectées via le fournisseur ?


## Annuaire
Référentiel permettant de stocker des données de manière hiérarchique et offrant des mécanismes pour rechercher efficacement l'information.

- Recensement des informations sur les utilisateurs, sur les applications, sur un parc informatique
- Authentifier un utilisateur
- Donner un droit d'un objet sur un autre, gestion de groupes (hierarchie)


**Technologies d'annuaires**
- OpenLDAP : projet opensource
- Active Directory (AD) : solution Microsoft.

## Ressource authentifié
Les besoins d'authentification sont variés.

Une application peut proposer :
- Du contenu public (donc sans authentification)
- Du contenu accessible à n'importe quel utilisateur authentifié
- Du contenu accessible à certains utilisateurs



- Authentification explicite : je présente un en-tête "Authorization" dans ma requête
- Authentification implicite : je m'attends à ce que l'application me donne un moyen de m'authentifier



En arrivant sur une page où je suis censé être authentifié
- Je présente un header "Authorization" ?
- Si oui et que l'application l'accepte je suis authentifié
- Sinon, l'application peut me proposer des moyens de m'authentifier
- S'il n'y a pas d'autres moyens que le header Authorization ou que j'ai déjà tenté tous les modes possibles sans succès, je reçois définitivement une erreur 401 "Unauthorized"


Si un ou plusieurs autre modes de connexion sont disponibles, les fonctionnements sont variés :
- 401 + WWW-Authenticate header : le navigateur doit savoir réagir
- 200 + traitements divers ou 30X : redirection vers page de login ou vers un fournisseur d'identité



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



Je suis correctement identifié mais la ressource ne s'adresse qu'à un certain type d'utilisateur dont je ne fais pas partie :
- Erreur 403 Forbidden

> Rappel code réponse HTTP :
> - 20X : succès
> - 30X : redirection
> - 40X : erreur côté Client Web
> - 50X : erreur côté Serveur Web


> - 200 : OK
> - 301 : Moved Permanently
> - 302 : Found (Déplacé temporairement)
> - 400 : Bad Request
> - 401 : Unauthorized
> - 403 : Forbidden
> - 404 : Not Found
> - 418 : I’m a teapot ([RFC 2324](https://www.ietf.org/rfc/rfc2324.txt))
> - 500 : Internal Server Error
> - 503 : Service Unavailable : réponse fournie par un reverse proxy si l'application n'est pas disponible (maintenance par exemple)


401 ou 403 ?
- 401 : Unauthorized : L'authentification ne s'est pas dérolée comme prévue ou elle s'est bien déroulée mais l'utilisateur est inconnu de l'application
- 403 : Forbidden : L'authentification s'est bien déroulée et l'utilisateur est connu mais il n'a pas les habilitations nécessaires


### Mise en pratique : comment savoir si l'utilisateur a les droits ?
Le mode d'authentification peut simplement présenter une identifiation. C'est alors à moi de déterminer si l'utilisateur a le droit d'accéder à la ressource et d'afficher les bonnes informations.

*M Robichu se connecte à mon questionnaire en ligne.*

*Il a pu s'authentifier correctement sur l'application, la seule information connue de l'application est alors son identifiant.*

*L'application recherche en base de données l'identifiant de M Robichu et récupère bien une ligne qui contient notamment les informations déjà renseignées par M Robichu.*

*Je sais donc que M Robichu a les droits sur l'application et je peux en plus préremplir le questionnaire avec ce que je sais déjà*


Le mode d'authentification peut fournir une réponse plus complète contenant des rôles. Je peux alors me servir de ces rôles récupérés pour gérer l'accès à mes ressources.

*M Robichu se connecte à mon questionnaire en ligne.*

*Il a pu s'authentifier correctement sur l'application, les informations connues de l'application sont maintenant son identifiant mais aussi une liste de rôles dont "repondant-enquete-satisfaction-joint-etancheite-climatiseur-morgue". *

*Je sais directement que M Robichu a les droits sur l'application. Mais rien ne m'empêche de chercher en base de données des informations plus précises sur M Robichu*

Rôles dans l'authentification ou en base de données ?

Se limiter au rôles le plus brut possible dans le système d'authentification (répondant, administrateur)

Éviter de trop spécifier le rôle surtout si l'on possède l'information en base de données. (répondant à l'enquête X, à l'enquête Y, etc.)

## Quelques exemples d'authentification
Authentifications directes :
- Basic
- Formulaire
- [Certificat Client](https://developer.mozilla.org/fr/docs/Introduction_%C3%A0_la_cryptographie_%C3%A0_clef_publique/Certificats_et_authentification)
- Kerberos : adapté au Web par SPNEGO, Simple and Protected GSSAPI (Generic Security Services Application Program Interface) Negotiation Mechanism, implémenté par AD
- NTLM (NT Lan Manager) : Systèmes Windows ou compatible AD

Authentifications centralisées nécessitant l'utilisation d'un autre mode :
- SAML
- OpenID Connect


### Kerberos
*WWW-Authenticate: Negociate*

- Système d'échange de jeton basé sur des clés symétriques (déduites des mots de passes)

https://blog.devensys.com/kerberos-principe-de-fonctionnement/


### NTLM
*Kerberos simplifié et moins sécurisé*

*WWW-Authenticate: NegociateNTLM*
- Envoi de l'identifiant
- Challenge du serveur, à résoudre avec le hash du mot de passe
- Le serveur demande à AD de valider (identifiant,challenge, réponse du client)
- AD répond au serveur si l'authentification est OK


### Authentifications Kerberos ou NTLM : Sécurité
- Afin de contrôler ce flux, les sources autorisées à émettre une demande de challenge sont contrôlées
- Dans Firefox, il s'agit des propriétés suivantes (about:config)

```
network.negotiate-auth.delegation-uris : insee.intra,insee.fr,insee.test,insee.eu,localhost
network.negotiate-auth.trusted-uris : insee.intra,insee.fr,insee.test,insee.eu,localhost
network.automatic-ntlm-auth.trusted-uris :
```

*Plus d'informations* : https://developer.mozilla.org/en-US/docs/Mozilla/Integrated_authentication


## Confidentialité de l'authentification
- Kerberos, NTLM, SAML : Sécurité intrinsèque (même sur un canal en clair, une lecture réseau ne permet pas d'obtenir des informations sur les credentials de l'utilisateur)
- Certificat client : Étape du protocole HTTPS
- Formulaire, Basic, OIDC : Sécurité basée sur un canal chiffré (HTTPS)



## Utilisation d'une fédération d'identité
- On verra le fonctionnement en détail plus tard (SAML, OpenIDConnect)
- Principe : déléguer l'authentification à un autre système (Kerberos et NTLM sont au fond des fédérations d'identité)
- Intérêts :
  - Authentification centralisée
  - Éviter les accès directs aux annuaires
  - SSO
  - Amélioration de la sécurité : la sécurité est également déléguée au protocole et au système authentifiant, reduction de la surface d'attaque
  - Cependant la sécurité du client reste à la charge des applications









