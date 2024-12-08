---
title : "Les annuaires LDAP"
description: ""
lead: ""
draft: false
images: []
weight: 604
mermaid: true
---

## Un annuaire

C'est base de donnée dans un format **hierarchique**, c'est à dire organisé sous la forme d'un arbre, avec des branches et des feuilles.

L'annuaire permet une performance accrue en lecture, recherche et authentification au détriment des opération d'écriture.

On désigne par annuaire LDAP les annuaires interrogeables par le protocole LDAP.

## Le protocole LDAP

LDAP ou Lightweight Directory Access Protocol définit un protocole de communication avec un annuaire mais définit par la même occasion un certains nombre de sépcification sur l'implémentation de l'annuaire interrogé.

Le protocole et spécifications associées sont définies dans les RFC 4510 et suivantes.

### Le protocole réseau

Le protocole LDAP est très fortement lié à la couche TCP. En particulier il n'y a pas d'autres notion de session que la session TCP (contrairement aux session HTTP par exemple).

Par défaut un annuaire LDAP écoute sur le port 389. Tout comme HTTP, LDAP est un protocole sans aucune sécurité de confidentialité intrinsèque, la confidentialité s'ajoute via le protocole TLS : STARTTLS en restant sur le port 389 ou LDAPS en écoutant par défaut sur le port 636.

Les différentes requêtes possibles sont :
- BIND : permet de soummettre l'identifiant d'un objet de l'annuaire et un mot de passe associé. En cas de réponse positive, cela signifie d'une part que l'identifiant et le mot de passe sont valide (validation des credntials d'un utilisateur), mais la suite des commandes LDAP de la session TCP **se fera avec les éventuels droits de l'objet authentifié**. *Il faut donc généralement interrompre immédiatement une communication LDAP qui n'aurait pour seul objet d'authentifier un compte après le BIND*. En l'absence de BIND sur une session LDAP, les requêtes sont réputées anonymes.
- UNBIND : il s'agit d'une fin de session LDAP (et non une simple déconnexion de l'utilisteur authentifié en BIND)
- SEARCH : permet de soumettre une recherche à l'annuaire. Une recherche se base sur un filtre de recherche. 
- COMPARE : requete de test d'une valeur d'attribut
- ADD : insère un nouvel objet dans l'annuaire
- MODIFY : modifie un objet existant
- MODIFY DN : déplace ou renomme un objet
- DELETE : supprime une feuille, c'est à dire un objet sans sous-entrée (la suppression de branche est une opération non standard proposée selon l'implémentation)

LDAP répond avec un système de code retour, par exemple la requête BIND se verra répondre :
- `0` si l'utilisateur a été trouvé et le mot de passe valide
- `49` si les informations transmises sont incorrectes (identifiant ou mot de passe invalide) 

### Nommage des objets

L'arborescence des objets de l'annuaire est appelée le DIT, Directory Information Tree.

Le DIT est composé d'une racine, correspondant à la base de l'arbre. Les objets peuvent ensuite ou non contenir des sous trées créant ansi des branches

```
dc=la,dc=racine
|
|-- ou=ma-branche
|   |-- ou=ma-sous-branche |
|   |                      |-- uid=toto
|   |                      |-- uid=tata
|   |-- ou=mon-autre-sous-branche |
|   |                             |-- cn=un truc
```

Chaque objet est identifié localement par un de ses attributs (par exemple `uid=toto`) attribut que l'on nomme alors **Relative Distinguished Name** ou RDN, cet attribut permet l'unicité de l'objet dans une branche donnée.
Un objet est ensuite désigné de façon unique par son **DistinguishedName** ou DN, concaténation des RDN qui permet de le retrouver dans l'arbre (par exemple `uid=toto,ou=ma-sous-branche,ou=ma-branche`)

### Typage des objets

Un objet désigne concrètement un ensemble d'attributs mono ou mutivalués.

Des notions de typage existent, on appelle **schema** l'ensemble des déclaration de types d'attribut possible et les classes d'objets réunissant ces attributs. Concrètement chaque object noeud ou feuille possède obligatoirement un ou plusieurs **ObjectClass**, chaque ObjectClass vient avec son lot d'attribut autorisés ou obligatoires.

```
ObjectClass: top
ObjectClass: person
uid: toto
sn: Dupond
givenName: Alfred
cn: Alfred Dupond
```

Un attribut est désigné dans le schéma par 
- son nom
- son OID (identifiant technique)
- sa syntaxe : texte brut, dn, telephone, mot de passe, binary, ...
- ses règle de recherche : attribut recherchable par simple existance, par chaîne exacte ou par sous chaîne (recherche avec jokers)
    - plus précisément la règle de recherche d'un attribut peut etre :
        - caseIgnoreMatch : egalité insensible à la casse
        - caseExactMatch : égalité sensible à la casse
        - caseIgnoreOrderingMatch : a surtout du sens pour les date entier, permet les <= et >=
        - caseIgnoreSubstringMatch : recherche avec joker (=\*toto\*)
- mono ou multivalué
- type user (attribut "métier") ou opérationnel (attribut "technique" généralement manipulé par le serveur comme la date de création, de dernière modification, date d'échec de mot de passe, ...)


Une classe d'object va donc ensuite définir quelles atributs sont obligatoire (MUST) ou possibles (MAY).

Les classes peuvent fonctionner par héritage, la classe va alors hériter d'instruction MUST et MAY et rajouter ses propres règles.

Les classes d'objet sont de 3 types :
- structurelle : chaque objet doit être d'une et d'une seule classe structurelle, une hierarchie de classe structurelles doit apparaitre dans les objectClass de l'objet
- abstraite : permet des héritage mais necessite une classe structurelle fille
- auxiliaire : permet de rajouter d'autres atributs sans interférer avec la hierarchie de classes pricipale

exemple de classe hierarchisée :
```
|-- top (abstraite), systématique
    |-- organization (structurelle)
    |-- organizationalUnit (structurelle)
    |-- person (structurelle)
    |   |-- organizationalPerson (structurelle)
    |       |-- inetOrgPerson (structurelle)
    |-- groupOfUniqueNames (structurelle)
|-- unAttributEnPlus (auxiliaire)
```

une combinaison d'objet possible pour mon objet toto est alors :
```
ObjectClass: top
ObjectClass: person
ObjectClass: organizationalPerson
ObjectClass: unAttributEnPlus
```

Les objets ont chacun une indépendance sur leur ObjectClass, appartenir à un branche n'entraine en aucun cas la reprise des objectClass de la racine de la branche


### La recherche LDAP

La recherche LDAP se définit en trois éléments :
- Le DN de la base de recherche, c'est à dire le noeud que l'on va considérer comme point d'entrée de la recherche. Mettre la racine comme DN de base va permettre une recherche dans tout l'annuaire. Mais on peut préférer ne rechercher que dans la branche des utilisateurs par exemple.
- Le scope :
   - BASE : on ne va recherceh que le DN de la base de recherche, en pratique cela sert à récupérer une entrée précise
   - ONE : permet de chercher dans la branche fille du noeud mis en base, sans recherche de sous branche et sans inclure la base
   - SUB : (Subtree) Cherche dans toute l'arborescence induite par la base de recherche, incluant la base elle même
- Le filtre : critères de recherche attribut par attribut, le filtre par défaut est `(ObjectClass=*)` qui matche avec tous les objets de l'annuaire

Syntaxe du filtre de recherche, les critères de recherches utilisables et leur fonctionnement dépendent des règles de recherche de l'attribut dans le schéma
- `(attribut=*)` recherche de l'existance d'un attribut sur l'objet
- `(attribut=valeur)` recherche de l'existance d'un attribut avec valeur exacte
- `(attribut=*valeur*avec*joker*)` recherche de l'existance d'un attribut avec recherche de sous chaines 
- `(attributDate<=20241204000000.0Z)` recherche en comparaison par exemple ici date avant la date demandée (les `<` et  `>` n'existent pas en syntaxe LDAP)
- `(!(attribut=valeur))` recherche négative
- `(&(attribut1=*)(attribut2=valeur1)(attribut3=*valeur*))` recherche "ET"
- `(|(attribut=valeur1)(attribut=valeur2)(attribut2=autrechose))` recherche "OU"

Le code retour du LDAP peut notamment être :
- `0` recherche OK (éventuellement avec 0 résultat)
- `32` le DN de base spécifié n'existe pas, l'annuaire va donner le "baest match" du DN fourni, soit le dernier RDN existant vraiment
- `4` si le nombre de résultats trouvé est supérieure à la limite configurée ou à la limite attribuée à l'utilisateur

Pour ce dernier cas, les annuaires peuvent implémenter la recherche paginée permettant de ne retourner qu'on nombre plus faible de résultat mais avec un cookie de recherche permettant de boucler sur des pages de résultats (spec Paged Search Control)


### Les modifications LDAP


Les modifications requétées sont unitaires
- Ajout d'une entrée avec contenu initial
- Suppression d'une entrée
- Modification d'une entrée :
   - Ajout d'un attribut (ou d'une valeur d'un attribut multivalué)
   - Modification de la valeur d'un atribut
   - Suppression d'un attribut

Les données et opérations de modification peuvent être décrites dans un format standard LDIF, LDAP Data Interchange Format

Le LDIF peut être dans 2 format :
- un format général permettant un dump d'un annuaire : toutes les entrées sont présentes avec tous leurs attributs, le LDIF ne peut être appliqué que sur un annuaire (ou un branche) vierge. Ce format sert notamment aux backups.
- un format différentiel permettant de décrire effectivement les modification appliquées (RFC 4525)

### Droits d'accès

Le opérations de lecture et d'écriture peuvent être gérés selon l'implémentation par des ACL (access list) décrivant finement les opération permises sur quoi (branche, attribut), pour faire quoi (lecture, écriture), par qui (anonyme, utilisateur précis, membre d'un groupe).

Des limites de recherche différentes peuvent également être défini par utilisateur et groupe d'utilisateur. 