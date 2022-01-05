<!-- .slide: class="slide" -->
## Digressions
- [La session HTTP](#/la-session-http)
- [Le fichier robots.txt](#/le-fichier-robots-txt)





<!-- .slide: class="slide" id="la-session-http" -->
### La session HTTP
- Dans le cas d'une utilisation par navigateur, on veut souvent conserver des informations sur l'utilisateur sur la durée de sa consultation : on dit que l'application est Stateful. (Au contraire un web service est stateless)





<!-- .slide: class="slide" -->
### La session HTTP
- Si l'authentification demande un identifiant/mot de passe dans un formulaire, renouveler l'opération à chaque opération peut provoquer une mauvaise expérience utilisateur.
- Si l'authentification est externe (fournisseur d'identité) le maintien d'une session est indispensable.






<!-- .slide: class="slide" -->
### La session HTTP
- Concrètement la session est un ensemble de donnée à usage court. 
- Dans le cas d'une authentification, un identifiant à usage unique attribué par le serveur permet de rappeler à chaque requête qui on est.
- D'une façon générale les données de session sont conservées côté serveur et le client ne possède qu'un identifiant de session.
- Cette identifiant est stocké dans le navigateur sous la forme d'un cookie.






<!-- .slide: class="slide" -->
### La session HTTP
- Si le serveur ne me connait pas encore (si je ne lui fournit pas de cookie), il m'en propose un :

```
Set-Cookie: JSESSIONID=ABCDEF123456; Secure; HttpOnly
```

- Pour "maintenir ma session", je dois rajouter ce cookie à chaque requête :

```
Cookie: JSESSIONID=ABCDEF123456
```






<!-- .slide: class="slide" -->
### La session HTTP
- Au bout d'un certain temps d'inactivité, le serveur supprime cet identifiant de session de sa mémoire. En cas de reconnexion, mon cookie est alors invalide, il m'en propose un nouveau, mais je ne suis donc plus connu de l'application (perte de l'authentification notamment).

Déconnecter un utilisateur = invalider son cookie de session.






<!-- .slide: class="slide" -->
### La session HTTP
Vol de session ?

Si j'arrive à récupérer le cookie de session de l'utilisateur je peux utiliser l'application en son nom (le temps de la session, mais c'est bien suffisant !)






<!-- .slide: class="slide" id="le-fichier-robots-txt" -->
## Le robots.txt
- Il sert à préciser au moteur de recherche les pages indexables
- Ce n'est pas une mesure de sécurité !! 
- N'importe qui autorisé peut toujours accéder à toutes les pages quelqusoit le robots.txt
- Il reste utile pour controler ce que les principaux moteurs de recherche vont indexer
- http://robots-txt.com/






<!-- .slide: class="slide" -->
## Le robots.txt
- Situation où tout le site ne doit pas être indexable (c'est le cas de keycloak par exemple)

```
User-agent: *
Disallow: /
```






<!-- .slide: class="slide" -->
## Le robots.txt
Configuration personnalisée
```
User-agent: *
Disallow: /context/
Disallow: /page.html

User-agent: Googlebot
Disallow: /context-interdit-a-google/
```