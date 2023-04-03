---
title : "Les attaques directes"
description: ""
lead: ""
draft: false
images: []
weight: 201
---


### L'injection de code "côté serveur"

<figure>
<img src="https://imgs.xkcd.com/comics/exploits_of_a_mom.png" width="100%" />
<figcaption><a href="https://xkcd.com/license.html">https://xkcd.com/license.html</a></figcaption>
</figure>

Que s'est-il passé ?

<script>
		var injection = function () {
			var body = document.getElementsByTagName("body")[0];
			var myTxt = document.getElementById("scriptEvil0").value;
			var displayText;
			// if (myTxt === "Robert'); DROP TABLE Students;--"){
			if (myTxt.indexOf("'") > -1) {
				displayText = "Oui, pour tester une attaque par injection on tente de mettre une ' dans le champ !";
			} else {
				displayText = myTxt + " a été ajouté en base";
			}
			myUl = document.getElementById("myUl0");
			node = document.createElement("LI");
			txt = document.createTextNode(displayText);
			node.appendChild(txt);
			myUl.appendChild(node);
		}
</script>

>#### École municipale de Kelkepare
>**Inscription d'un nouvel élève**
>
>Prénom : <input id="scriptEvil0" type="text" /> <button onClick="injection();" >Inscrire l'élève</button>
><ul id="myUl0"></ul>

Le code ressemble à ça :

```java
public void inscrireEleve(String prenom){
  String maRequeteSQL = "INSERT INTO Students VALUES ('" + prenom + "')";
  connectionSql.createStatement().executeQuery(maRequeteSQL);
}
```

Si on remplit dans le formulaire `Robert'); DROP TABLE Students;--`
La requète envoyée au serveur devient : 

```
INSERT INTO Students VALUES ('Robert'); DROP TABLE Students;--')
```

**Injection SQL**

Pourquoi ?
- Consulter des données confidentielles
- Altérer la base de données  

**Attaque DIRECTE**


On remarque que ce type d'attaque se produit quand un langage fait appel à un autre langage.
Un code java par exemple lance des requetes sql ou ldap moyennant ces chaines de caracteres.


```java
"SELECT * FROM USER WHERE identifiant='" + username + "' AND password='" + password + "'";
```
à renseigner avec username=`robichu` et password=`' OR '1'='1' --` pour se connecter en tant que M Robichu

La sécurité va donc consister essentiellement à repérer ces possibles manipulation de la chaînes, c'est à dire repérer les **caracteres spéciaux du langage cible** et les échapper selon les modes d'échappement du langages.

On réécriera `' OR '1'='1' --` en `'' OR ''1''=''1'' --` 

**Protection ACTIVE**

Concrètement, on n'est pas les premieres à se dire qu'il faut échapper des caractères, il faut s'appuyer au maximum sur des bibliotheques éprouvées.
Toujours dans le cas de sql :
- Utiliser des PreparedStatement 
- Mieux, Utiliser des Frameworks qui font le boulot sans que l'on gère vraiment le sql (hibernate, ...)
- **Des bibliothèques que l'on garde à jour bien entendu**



### Un peu de pratique...
Découvrons une série challenge disponible sur internet
- [Natas 0](http://natas0.natas.labs.overthewire.org) (mot de passe de natas0 : `natas0`)
- Sur les premiers niveaux, on voit qu'une resource cachée n'est jamais cachée (<a href="/securite-applicative/digressions.html#/le-fichier-robots-txt" target="_blank">Digression : robots.txt</a>) et que quelques éléments pouvent être très facilement manipulés dans la requête

### L'injection de code : le retour de la revanche
Quelques niveaux plus tard :
- [Natas 12](http://natas12.natas.labs.overthewire.org) (mot de passe de natas12 : `EDXp0pS26wLKHZy1rDBPUZk0RKfLGIR3`)
- Le but est de découvrir le mot de passe de natas13 situé dans /etc/natas_webpass/natas13
- Hint : passthru est une fonction php permettant d'éxécuter sur le sytème du code arbitraire <!-- .element: class="fragment"-->

Exécution de code sur le serveur

- Mettre un fichier malicieux sur le serveur permettant l'éxécution de code à distance.
- Ici le code injecté sera du code **éxécuté côté serveur** (java sur jsp, php)


**L'injection de code "côté serveur"**
- Exécution de code sur le serveur au plus près du système
- Rebond sur le SI possible si :
  - flux larges entre les différents serveurs
  - compte éxécutant le code à haut privilège


- Nombreuses possibilités : aussi bien des traitements dans l'application non prévus que des opérations directement sur le système d'exploitation
- Par exemple réussir à placer sur le serveur une jsp contenant :
```jsp
<%Runtime.getRuntime().exec("less /confidentiel/data");%>
```
- Un langage de programmation lance indirectement des commandes système

## Sécurité :
- Contrôler les entrées utilisateurs et en particulier  pour les **fichiers** : 
  - le type MIME des entrées, la cohérence type attendu et contenu autant que possible
  - l'extention de fichier nommé car le serveur et/ou le navigateur peut interpréter différemment un fichier selon son nommage


### Sécurité :
- Côté prod : 
  - "chroot" des applications ou machines limité à un traitement
  - utilisateur éxécutant l'application avec droit limités (ce n'est pas le compte root/admin qui démarre le tomcat)
  - flux limités entre machines


### L'injection de code : Synthèse
- Un sujet central : l'entrée untilisateur


Mais qu'est ce qu'une entrée utilisateur ?
- Requête et Paramètres de requête
- Corps de requête (contenu des requête POST et PUT) : réponse à un formulaire, fichier
- Cookies
- [Entetes HTTP](https://developer.mozilla.org/fr/docs/Web/HTTP/Headers) (Authorization, Location, Host, User-Agent, Referrer, Cookies, CORS, ...)


Controle des chaines entrées et échappement de tous les caratères interprétables en html, en sql,... selon la situation.

Un développeur web peut se contenter de surveiller ces entrées mais en réalité, les failles peuvent survenir sur n'importe quel bit d'un échange réseau.



## Une attaque réelle pour illustrer

- Log4Shell *Decembre 2021*
- https://fr.wikipedia.org/wiki/Log4Shell
- Voir projet exploit
- Exploiter avec https://canarytokens.org/generate


