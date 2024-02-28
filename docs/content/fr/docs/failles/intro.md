---
title : "Qu'est ce qu'une faille applicative ?"
description: ""
lead: ""
draft: false
images: []
weight: 200
---

- Demandons l'avis à **[Wikipedia](https://fr.wikipedia.org/wiki/Vuln%C3%A9rabilit%C3%A9_(informatique))** :

> *Une vulnérabilité ou faille est une faiblesse dans un système informatique permettant à un attaquant de porter atteinte à l'intégrité de ce système, c'est-à-dire à son fonctionnement normal, à la confidentialité ou à l'intégrité des données qu'il contient*


- Un avis ChatGpt ?

> Une faille applicative est une vulnérabilité dans une application informatique qui permet à un attaquant d'exploiter une faiblesse dans le code ou la configuration de l'application pour obtenir un accès non autorisé, modifier des données ou exécuter des actions malveillantes.

> Les failles applicatives peuvent être causées par une mauvaise conception de l'application, des erreurs de programmation, des problèmes de configuration ou des erreurs humaines. Certaines des vulnérabilités les plus courantes comprennent les injections SQL, les attaques de scripts entre sites (XSS), les attaques de falsification de demande intersite (CSRF), les failles d'authentification, les failles de contrôle d'accès, les erreurs de validation de données, etc.

> Il est important pour les développeurs et les équipes de sécurité informatique de détecter et de corriger rapidement les failles applicatives pour éviter que des attaquants ne profitent de ces vulnérabilités. Les tests de sécurité réguliers, les examens de code et les audits de sécurité sont des moyens courants de détection et de correction des failles applicatives.


Grandes catégories :

- Une erreur de conception ?
- Une erreur de configuration ?
- Une erreur de programmation (bug, fonctionnalité mal implémentée) ?



## Une erreur de conception ?

Cas 1 :

*Soit une plateforme de réservation en ligne pour des chambres d'hôtel.*

*Je propose aux utilisateurs d'annuler leurs commandes jusqu'à 16 h.*

*Un concurrent peut facilement pourrir mon hôtel en réservant toutes les chambres et en les annulant à 15h55.*

Est ce que cela fait partie du **fonctionnement normal** ?


Cas 2 :

*Sur mon web service de gestion d'annuaire, on peut créer autant d'habilitations que l'on veut pour un contact*

Au delà d'environ 300 habilitations c'est l'annuaire qui finit par planter...



Problématiques à la conception :
- S'assurer que **par design** certains processus ne seront pas forcément soumis à des instabilités
- Connaître les limites de son système


## Une erreur de configuration ?

Quelques exemples :

- L'application n'est pas proposée et forcée en HTTPS
- Flux trop large en entrée (par exemple possiblité de connexion sur ports d'administration ou base de données exposée)
- Secrets exposés
- Bibliothèques à jour mais avec une configuration permettant de faire n'importe quoi (`mode.debug=true` ou `conf.developpement=true`)
- ...


## Une erreur de programmation ?
- Son propre code
- Le code des autres, dit autrement les bibliothèques utilisées : Nécéssité de mise à jour régulière !


**Le top 10 des vulnérabilités : OWASP**

[https://owasp.org/www-project-top-ten/](https://owasp.org/www-project-top-ten/)


- A1 : Acceder à une ressource non prévue :
  - Mauvaise gestion des droits
  - Forcer un utilisateur privilégié à cliquer sur l'action souhaitée 
    - lié aux failles XSS (injection de code "côté client"), vol de session
    - CORS trop permissif
  - Manque de tracabilité
- A2 : Ne pas ou mal utiliser un canal chiffré
  - Protocoles obsolètes, contrôles non effectués
  - HTTPS non obligatoire
- A3 : Injection de code 
  - "côté serveur"
    - Faire éxécuter du code non prévu par exemple directement sur la base de données
  - "coté client"
    - XSS
- A4 : Erreur de conception
  - La meilleure des implémentations ne corrigera pas
- A5 : Erreur de configuration
- A6 : Bibliothèques pas à jour (ou utilisation de bibliothèques abandonnées)
  - Bugs des "autres"
- A7 : Failblesses d'authentification
  - Possibilité de brute force
  - Mots de passe faibles ou trop standards
  - Déconnexions et invalidation de session mal gérés
- A8 : Non controles d'intégrité du build
  - Bibliothèques vérolées (au build)
- A9 : Défaut de tracabilité
  - Logs inexploitables
  - Impossible de savoir clairement qui a fait quoi et quand
- A10 : Injection de code "côté serveur", vision réseau
  - Sécurité en profondeur, cloisonnement réseau
  - Comptes applicatif à droits limités


## Une petite typologie d'attaque et de protection

*Disclaimer* : Cette typologie est volontairement très réductrice !

On différenciera par la suite deux grands types d'attaque et deux grand types de protection.

### Les attaques directes

L'attaquant va directement taper sur le serveur cible.

### Les attaques indirectes

L'attaquant va forcer un utilisateur avec les droits requis à faire l'action pour lui.

### Les protections actives

On rajoute des entraves sur l'accès au serveur qui serait clible de l'attaque.

### Les protection passives ou déclaratives

Je vais donner à l'utilisateur (et notamment au navigateur de l'utilisateur !) des règles sur ce à quoi ressemble une requête légitime, pour détecter et bloquer tout ce qui en diverge.

### Illustration

Pour illustrer, imaginons hors domaine informatique que je suis un vil voleur souhaitant m'emparer d'un lingot d'or dans le coffre fort de M Alphonse Robichu.

**Les attaques directes**

- Je pénètre dans la banque, je met un baton de dynamite, le coffre explose, je récupère le lingot.

**Les attaques indirectes**

- Je vais voir un préposé de la banque qui stocke le coffre fort, je sais qu'il possède une clé admin qui ouvre tous les coffres. Je lui dit que je suis Alphonse Robichu et que je souhaite qu'il aille récupérer le lingot pour moi dans le coffre.

**Les protections actives**

- Je change la structure de mes coffres pour qu'ils soient à l'épreuve de la dynamite.

On remarquera que cette protection est totalement inefficace sur l'attaque "indirecte" précédente

**Les protection passives**

- J'explique au préposé que seul les clients muni d'une carte d'identité valide peuvent faire ce genre de demande.

Ma protection ne marche que si j'ai confiance dans le respect du processus par mon préposé



### S'initier aux failles applicatives
- [OWASP](https://www.owasp.org)
  - Documentation et notamment le OWASP top 10
  - WebGoat (découverte de failles sous forme de cours)
  - Juice Store (challenges)
- [Natas](https://overthewire.org/wargames/natas/)
  - [Natas 0](http://natas0.natas.labs.overthewire.org) (mot de passe de natas0 : `natas0`)
  - Sur les premiers niveaux, on voit par exemple qu'une resource cachée n'est jamais cachée (<a href="/securite-applicative/digressions.html#/le-fichier-robots-txt" target="_blank">Digression : robots.txt</a>) et que quelques éléments pouvent être très facilement manipulés dans la requête




