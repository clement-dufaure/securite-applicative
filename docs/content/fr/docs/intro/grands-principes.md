---
title: "Les grands principes de la sécurité"
description: ""
lead: ""
draft: false
images: []
menu:
  docs:
    parent: "intro"
toc: true
weight: 102
---

Qu'est ce que "la sécurité infromatique" ??

ou *De quoi va-t-on parler ?*

Des grands principes (nombre et portée variant selon les sources)

- Disponibilité
- Intégrité
- Confidentialité
- Authentification
- Traçabilité
- Non répudiation

> La première partie de cette formation présentera des [failles applicatives](/docs/failles/intro/) pouvant impacter un ou plusieurs de ces principes.


## Disponibilité
- S'assurer que les utilisateurs peuvent accéder au service sur les plages d'utilisation prévues et en respectant des temps de réponse attendus
- Du ressort des ops...
  - Haute disponibilité via plusieurs couloirs (scalabilité horizontale)
  - Ajutement mémoire/CPU (scalabilité verticale)
  - Supervision
- ... et des devs
  - Penser les limites de chaque traitement, contraindre l'utilisateur
  - Éviter les traitements saturant la machines en une requête...
  - Suivre les mises à jour des librairies

## Intégrité
- S'assurer que l'information envoyée à l'utilisateur est celle qu'il reçoit
- Solutions pour considérations techniques (réseau perturbé, perte de paquet, ...)
  - Ajout de mécanismes de controles : TCP avec numéro de séquence et checksum
  - Notions de fonctions de hachage
- MAIS : ne permet pas de se prémunir d'une altération volontaire des données par un attaquant
  - Ajouter un hash... mais non modifiable facilement... donc ne transitant pas en clair

## Confidentialité
- S'assurer que seul l'emmetteur et le récepteur d'une information peuvent la consulter
- S'assurer de l'identité du serveur distant
- Nécessité de chiffrer les données
- TLS surchargé aux principaux protocoles (HTTPS, FTPS, etc.)

{{< alert icon="👉" text="L'intégrité est très difficilement dissociable de la confidentialité <br/> <b>Une appli sans HTTPS n'est plus envisageable aujourd'hui</b>" />}}


> Un kit de survie du HTTPS sera abordé dans la partie [HTTPS](/docs/https) de la formation. Il s'agit de connaitre les principes de base, afin de pouvoir apréhender les principales erreurs relative à une configuration HTTPS.


## Authentification
- Une opération n'est accessible qu'à ceux qui sont censé réaliser l'opération
- Prouver qu'on est celui qu'on déclare être

> Une grande partie de cette formation se focalise sur les enjeux et les moyens concret de mettre en place une authentification sur son application


## Traçabilité
- Être capable de savoir ce qui s'est passé sur un serveur et par qui
- Avoir un système de logs,... Connaitre son sytème de logs !!!
- Avoirs des moyens efficace de rechercher une information dans ses logs

> Ne sera pas abordé dans cette formation, mais reste une étape indispensable à la reflexion de la sécurité de son application. Le jour on aura besoin de savoir ce qu'il s'est passé, ce sera trop tard pour y penser...


## Non répudiation
- Un utilisateur ne peut nier une action effectuée
- "Signature d'un contrat"
- Principe de la signature électronique

> Hors scope de cette formation


## Pour aller plus loin
- Auto-formation aux grands concepts de la sécurité par l'ANSSI :
[https://www.secnumacademie.gouv.fr/](https://www.secnumacademie.gouv.fr/)


