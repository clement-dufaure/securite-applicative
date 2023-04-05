---
title : "L'authentification des clients d'API"
description: ""
lead: ""
draft: false
images: []
weight: 602
mermaid: true
---

## Stratégies Utilisateur -> Application intermédiaire -> API

**Stratégie 1**
- L'application intermédiaire possède le compte admin de l'API
- L'application intermédiaire authentifie "indépendemment" l'utilisateur et est garant de la sécurité des traitement
- Pour l'API, c'est un admin qui s'est connecté même si l'utilisateur final a des droits très limités
- Faille sur l'application intermédiaire = élévation de privilèges


**Stratégie 2**
- L'application intermédiaire demande une information d'authentification acceptée par l'API
- (L'application intermédiaire peut s'en servir pour ses propres traitements)
- L'application intermédiaire transmet cette information à l'API
- Pour l'API, c'est l'utilisateur qui s'est connecté et pas un admin
- L'application intermédiaire ne peut plus être responsable d'attaque par élévation de privilège


## Transférer une authentification ?
- L'authentification repose sur un secret
- Plus un secret a de gardien moins il est gardé
- Transférer le secret = mauvaise idée
- Une seule application va gérer ce secret, et va établir un "jeton" ou "token" qui garantie l'identité
- Les autres applications se transmettent ce token


## Authentification avec Javascript
- Frontend en javascript (tout se passe dans le navigateur)
- AUCUNE protection n'est nécessaire
- Pourquoi on authentifie en js ?
    - Affichage
    - Transmettre l'authentification au backend


- Stratégie 1 innaplicable
- On ne peut pas garder un secret en javascript
- La javascript ne va pas vraiment contôler l'authentification mais la stocker et la transmettre
- Aspect sécurité : Stockage du jeton ? 



## Un script R (client lourd en général) doit se connecter à une API ?
- Il s'agit également de récupérer une information d'authentification pour la transmettre
- On n'est plus dans un navigateur, seul le mode de récupération du token va changer



## Un batch doit se connecter à une API authentifiée ?
- Pas d'utilisateur final
- => pseudo "Stratégie 1" : on va se connecter à l'API au nom de l'application et non pas au nom d'un utilisateur
- Cependant on construit la Stratégie 2 pour les situations "utilisateurs" et on adapte la solution batch à l'architecture "stratégie 2"
- => on utilise quand même la solution token

