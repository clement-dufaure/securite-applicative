<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 0
### Les concepts de la sécurité





<!-- .slide: class="slide" -->
### Les grands principes
- Disponibilité
- Intégrité
- Confidentialité
- Authentification
- Traçabilité
- Non répudiation





<!-- .slide: class="slide" -->
### Disponibilité
- S'assurer que les utilisateurs peuvent accéder au service sur les plages d'utilisation prévues et en respectant des temps de réponse attendus
- Du ressort de la prod ?
- Pas seulement ! <!-- .element: class="fragment" -->
- Des failles applicatives peuvent saturer la machine <!-- .element: class="fragment"-->





<!-- .slide: class="slide" -->
### Disponibilité
#### Côté prod :
- Haute disponibilité via trois couloirs
- Supervision, ajutement mémoire/CPU


#### Côté développeurs
- Penser les limites de chaque traitement, contraindre l'utilisateur
- Suivre les mises à jour des librairies






<!-- .slide: class="slide" -->
### Intégrité
- S'assurer que l'information envoyée à l'utilisateur est celle qu'il reçoit
- Ajout de mécanismes de controles : TCP avec numéro de séquence et checksum
- Permet de se prémunir de problème techniques réseau (perte de paquet)
- Ne permet pas de se prémunir d'une altération volontaire des données par un attaquant : l'intégrité est indissociable de la Confidentialité
- Utilisation de fonction de hachage





<!-- .slide: class="slide" -->
### Confidentialité
- S'assurer que seul l'emmetteur et le récepteur d'une information peuvent la consulter
- Nécessité de chiffrer les données
- TLS surchargé aux principaux protocoles (HTTPS, FTPS, etc.)





<!-- .slide: class="slide" -->
### Une appli sans HTTPS n'est plus envisageable aujourd'hui
![httpseverywhere](./images/httpseverywhere.jpg)





<!-- .slide: class="slide" -->
### Confidentialité
- Le TLS (nouveau nom de SSL) consiste à générer et partager une clé symétrique
- Un système clé privée/clé publique permet en plus de s'assurer de l'identité du serveur que l'on contacte
- Un certificat regroupe une clé publique, une identité physique et une validation par une autorité de confiance (signature)
- En option le client peut utiliser lui aussi un couple clé privée/ clé publique pour s'authentifier





<!-- .slide: class="slide" -->
### Authentification
- Prouver qu'on est celui qu'on déclare être





<!-- .slide: class="slide" -->
### Authentification
Les facteurs d'authentification
- Ce que je sais : mot de passe
- Ce que je possède : certificat, carte à puce, badge
- Ce que je suis : empreinte digitale, rétinienne





<!-- .slide: class="slide" -->
### Traçabilité
- Être capable de savoir ce qui s'est passé sur un serveur et par qui
- Durée légale de conservation des logs d'accès à un service : 1 an





<!-- .slide: class="slide" -->
### Non répudiation
- Un utilisateur ne peut nier une action effectuée
- "Signature d'un contrat"
- Principe de la signature électonique






<!-- .slide: class="slide" -->
### Auto-formation aux grands concepts de la sécurité
- Par l'ANSSI :
https://www.secnumacademie.gouv.fr/
