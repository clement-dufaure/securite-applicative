---
title : "Un cookie ?"
description: ""
lead: ""
draft: false
images: []
weight: 203
---

## Qu'est ce que c'est... dans une requête HTTP ?

- Demande de stockage sur le poste utilisateur
- Petite taille : chaîne de caractères
- Sert à mémoriser des informations entre plusieurs requêtes HTTP

```
Set-cookie: la-donnee-a-sauvegarder;metadonnees
```

Pourquoi des cookies ? 
- Cookies de session : suivi de l'utilisateur pendant sa visite d'un site, devrait expirer à la fermeture du navigateur. 
- Cookies persistants : tracage long de l'utilisateur, reconnexion automatiques, stockage avec date d'expiration explicite plus longue que la femreture du navigateur

Il s'agit souvent d'identifiants partagés avec le serveur. Le serveur possède une table de session, à chaque requête, l'utilisateur en renvoyant son cookie donne l'identifiant, le serveur retourve les données utilisateurs associées. Il retourve notemment l'identifiant de connexion et habilitations le cas échéant.

```
Cookie: la-donnee-a-sauvegarder;metadonnees
```

Tant que l'identifiant est connu du serveur, **un cookie est aussi sensible que les informations d'authentification de l'utilisateur**

Les cookies servent aussi de façon plus générale à retenir des préférences utilisateurs ou à analyser et suivre l'utilisateur à des fins analytiques ou publicitaires.


## Fonctionnement

C'est le navigateur qui gère nativement les ccokies. Une entrée `Set-Cookie` enregistre le cookie pour le site concerné. Sur chaque requête sur le même site, le navigateur renverra un header `Cookie` avec la valeur demandée par le serveur.

Est-ce toujours légitime ?
- Si la requête est faite en direct (url demandée par l'utilisateur), a priori oui
- Si la requête a été générée depuis la page web d'un autre site, pas forcément...

exemple :

Le site de l'attaquant forge via un script une requête vers le site à attaquer. Si la requête est lancée avec les cookies de session, la requête sera exécuté avec les droits de l'utilisateur, ce qui peut déclencher une action non souhaitée.

Lorsqu'un cookie d'un site est envoyée dans une requête émise depuis un autre site on parle de **cookie tiers**.

Par ailleurs le cookie étant une chaîne enregistrée dans le navigateur, elle peut être requétée directement via un script.

```
document.cookie
```

Avant que les navigateurs ne propose autre chose, c'était le moyen de stocker des variables pour un script.

## Métadonnées d'un cookie

```
set-cookie abcdef; expires=Mon, 08-Dec-2025 21:18:32 GMT; path=/; domain=mon.site; Secure; HttpOnly; priority=high; SameSite=none
```

- La date ou durée d'expiration : `expires` ou  `Max-Age`, par defaut `Session` c'est à dire non persisté après fermeture du navigateur
- Des précisions sur le domaine et path de définition du cookie (par défaut domaine et path de la page qui a fourni le cookie). Peut permettre d'élargir le champ du cookie. Tout appel HTTP issue de script ou iframe depuis une page hors champ rendra le cookie comme **cookie tiers**.
- Flag `Secure` : le cookie ne sera transféré que sur canal sécurisé, soit HTTPS
- Flag `HttpOnly` : le cookie ne peut pas être directement requêté dans un script
- Policy `SameSite` : donne la politique vis à vis de l'usage en tant que cookie tiers lors de requêtes intersite

La politique `SameSite` :
- Strict : Le cookie n'est envoyé qu'avec des requêtes provenant du même site.
- Lax : Autorise l'envoi lors des requêtes de navigation initiales, c'est à dire les GET changeant directement l'url de la barre du navigateur. Par exemple dans un forulaire en POST ou un fetch(), le cookie ne sera pas transféré.
- None : Le cookie est envoyé avec toutes les requêtes intersites, GET, POST, fetch(), ..., cette policy nécessite également le flag `Secure` (dans un navigateur récent).

Reference : https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie

