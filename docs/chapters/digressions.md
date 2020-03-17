<!-- .slide: class="slide" -->
## Digression : Le robots.txt
- Il sert à préciser au moteur de recherche les pages indexables
- Ce n'est pas une mesure de sécurité !! 
- N'importe qui autorisé peut toujours accéder à toutes les pages quelqusoit le robots.txt
- Il reste utile pour controler ce que les principaux moteurs de recherche vont indexer
- http://robots-txt.com/






<!-- .slide: class="slide" -->
## Digression : Le robots.txt
- Situation où tout le site ne doit pas être indexable (c'est le cas de keycloak par exemple)
```
User-agent: *
Disallow: /
```






<!-- .slide: class="slide" -->
## Digression : Le robots.txt
Configuration personnalisée
```
User-agent: *
Disallow: /context/
Disallow: /page.html

User-agent: Googlebot
Disallow: /context-interdit-a-google/
```