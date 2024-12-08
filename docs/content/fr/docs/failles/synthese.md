---
title : "Synthèse"
description: ""
lead: ""
draft: false
images: []
weight: 204
---

Deux grands types de protection
- Vérifier les entrées utilisateurs ! (Sécurité active)
- Vérifier que l'utilisateur est bien conscient de ses actions :
  - Vérification d'état (Sécurité active)
  - Dire au navigateur quelles opérations sont légitimes ou non (Sécurité déclarative)

# Synthèse des header déclaratifs :

Il est **nécessaire** de rajouter :

- Le blocage des iframe sauf si on sait ce que l'on fait, par exemple pour un besoin fonctionnel ou pour un site totalement public
```
X-Frame-Options: DENY
```

- L'obligation de passer en HTTPS sans possiblité de dérrogation pour les futurs accès au site :
```
Strict-Transport-Security: max-age=63072000
```

En complément la CSP
```
Content-Security-Policy: upgrade-insecure-requests;
```
permet de forcer le Https en cas d'accès http

Il **ne faut pas** ajouter :

- De façon systématique
```
Access-Control-Allow-Origin: *
```
Sauf si le site est public sans données confidentielles même "internes", c'est à dire que l'on part du principe que le site peut etre pleinement requêté de n'importe où sans restrictions

- Ce header qui n'est plus supporté et potentiellement sujet à des attaques :
```
X-XSS-Protection
```

Il est **recommandé** de rajouter

- L'en-tête CSP avec une configuration à définir, par exmple :
```
Content-Security-Policy: default-src 'none'; script-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self'; frame-ancestors 'none'; form-action 'self';
```
    - n'accepte que les scripts, images et styles ayant le même hôte que la page appelée, ainsi que les pages appelées via XmlHttpRequest, fetch ou assimilé
    - désactive les fonctions javascript de type "eval"
    - désactive les scripts inline (peut etre réactivé unitairement via )
    - n'autorise pas l'inclusion de la page dans un iframe (redondant avec X-Frame-Options, mais l'entete CSP a vocation à remplacer les autres déclarations)
    - n'autorise que les actions de formulaire à destination du même hôte que la page appelée



**WARNING** 

Sous réserve de savoir ce que l'on fait, on peut enrichir sa conf HSTS des éléments suivants :
```
Strict-Transport-Security: max-age=63072000; includeSubDomains; preload
```
Ce qui aura pour effet de forcer le HTTPS sur l'ensemble des sous domaines, et de l'enregistrer dans les sites "preload" c'est à dire préalablement à la première connexion pour les navigateur qui supportent cette liste "preload" (Géré par google, incluse a minima dans Chrome)


# Les dépendances

## Les CVE (Common Vulnerabilities and Exposures)

Les communautés/entreprises publient des alertes de sécurité observées sur leurs produits sous forme de CVE.
Le [CERT-FR](https://www.cert.ssi.gouv.fr/) est un sytème de surveillances de ces failles applicatives. Il publie aussi régulièrement des articles de cybersécurité.

Une CVE éventuellement complété par CERT-FR contient notemment :
- Le contexte de la faille => une faille peut s'appliquer à un composant que l'on possède mais pas dans une configuration active dans notre contexte
- L'impact de la faille => des scores (notamment CVSS, entre 0 et 10) sont définis pour quantifier le niveau de risque induit
- Les versions concernées, et éventuellement les version publiées qui corrigent (souvent les communautés vont développer le correctif en interne, le publier et publier la CVE)
- Les contournements possibles : désactiver une partie du composant, ajouter un filtrage réseau externe,... 

Les CVEs sont généralement visibles sur les repository de dépendances.
Exemple : https://mvnrepository.com/artifact/org.postgresql/postgresql/42.7.1

## Surveiller les dépendances obsolètes
- Dependabot ou Renovate  : vérification existance d'une nouvelle version
- Pour vérifier vulnérabilités connues : https://owasp.org/www-project-dependency-check/
