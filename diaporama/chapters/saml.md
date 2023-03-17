<!-- .slide: data-background-image="images/securite-informatique.png" data-background-size="1200px" class="chapter" -->
## 6
### Saml





<!-- .slide: class="slide" -->
### Les grands principes
Security Assertion Markup Language ou *langage de balisage d'assertion de sécurité*
- Dédié aux applis Web
- L'utilisateur est au coeur de son authentification
- Jeton échangé via XML







<!-- .slide: class="slide" -->
### Les grands principes
- L'application cliente (Service Provider ou SP) demande une authentification
- Elle redirige l'utilisateur vers un IDP ou Identity Provider, en transmettant une requête d'authentification donnant notemment des informations sur le SP. Ce n'est pas obligatoire dans le protocole mais la requête doit être signée (clé privée du SP) et chiffrée (clé publique de l'IDP)
- L'IDP recherche le SP appelant, vérifie la signature et cherche à authentifier l'utilisateur
- En cas de réussite, il redirige vers le SP avec une assertion qu'il signe (clé privée de l'IDP) et chiffre (clé publique du SP).
- Le SP valide l'assertion









<!-- .slide: class="slide" -->
### Les grands principes
<img src="./images/saml.png" width="70%" />
<a title="Creative Commons Attribution-ShareAlike 3.0" href="https://creativecommons.org/licenses/by-sa/3.0/">CC BY-SA 3.0</a>, <a href="https://en.wikipedia.org/w/index.php?curid=32521419">Link</a>










<!-- .slide: class="slide" -->
###  Du SAML à l'ère d'OpenIdConnect  ?
- Nécessaire si l'application cliente et le serveur d'authentification ne sont pas connectés directement (Un en interne, un en DMZ)
- Applications atypiques ne supportant que SAML (progiciel,...)







<!-- .slide: class="slide" -->
### Contenu d'une configuration SAML
- Les fichiers metadatas du SP et des IDP : éléments centraux des configuration SAML
- Un keystore contenant la clé privé et le certificat du Service provider : permet de signer les requêtes et de déchiffrer les réponses
- Selon les implémentations, il faut extraire des informations des fichiers metadatas et les présenter comme des paramètres. 










<!-- .slide: class="slide" -->
### Le metadata SAML
- Une application cliente connait un ou plusieurs IDP
- Un IDP connait une liste d'application

Un moyen simple de s'échanger les informations sur un client ou un IDP est le metadata








<!-- .slide: class="slide" -->
### Metadata d'un SP
```xml
<md:EntityDescriptor ...  entityID="https://localhost:8443">
   <md:SPSSODescriptor AuthnRequestsSigned="true" WantAssertionsSigned="true" ...>
      <md:KeyDescriptor use="signing"><ds:KeyInfo ...><ds:X509Data><ds:X509Certificate>Certificat du SP (clé plublique)</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor>
      <md:KeyDescriptor use="encryption"><ds:KeyInfo ...><ds:X509Data><ds:X509Certificate>Certificat du SP (clé plublique)</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor>
      <!-- Indique où rediriger la requête dans le cas d'une demande de SLO selon le type de bind -->
      <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://localhost:8443/saml/LogoutServiceHTTPRedirect" ResponseLocation="https://localhost:8443/saml/LogoutServiceHTTPRedirectResponse" />
      <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="https://localhost:8443/saml/LogoutServiceHTTPPost" ResponseLocation="https://localhost:8443/saml/LogoutServiceHTTPRedirectResponse" />
      <!-- Format du "Name ID" qui est l'identifiant selon le fournisseur d'identité. Dans le cas "transcient", c'est une chaine aléatoire pour chaque assertion -->
      <md:NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</md:NameIDFormat>
      <!-- Indique où poster la demande d'assertion -->
      <md:AssertionConsumerService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="https://localhost:8443/saml/SAMLAssertionConsumer" index="0" isDefault="true" />
      <md:Extensions>
        <!-- Informations destinées à configurer la page de login dans le cas de la fédération externe -->
         <mdui:UIInfo>
			        <mdui:DisplayName xml:lang="fr"></mdui:DisplayName><mdui:DisplayName xml:lang="en"></mdui:DisplayName><mdui:Description xml:lang="fr"></mdui:Description><mdui:Description xml:lang="en"></mdui:Description><mdui:Logo height="320" width="75"></mdui:Logo><mdui:InformationURL xml:lang="fr"></mdui:InformationURL><mdui:InformationURL xml:lang="en"></mdui:InformationURL>
        </mdui:UIInfo>
      </md:Extensions>
   </md:SPSSODescriptor>
   <md:Organization>...</md:Organization><md:ContactPerson ...>...</md:ContactPerson>
</md:EntityDescriptor>
```






<!-- .slide: class="slide" -->
### Metadata d'un SP
- Attention les Endpoints doivent être identique sur la conf du SP et tel que déclarés sur l'IDP.
- C'est une vérification pour ne pas envoyer l'utilisateur n'importe où (Le SP redonne l'url de redirection lors d'une requête d'authentification)







<!-- .slide: class="slide" -->
### Metadata d'un idp

Récupérable sur chaque fédération sur /idp/shibboleth

```XML
<EntityDescriptor ... entityID="https://idp.test/idp/shibboleth">
  <IDPSSODescriptor ...>
    <KeyDescriptor><ds:KeyInfo><ds:X509Data><ds:X509Certificate>certificat de l'idp (clé publique)</ds:X509Certificate></ds:X509Data></ds:KeyInfo></KeyDescriptor>
    <!-- Services proposés, type de bind (POST, Redirect, SOAP, ...) et url associées en particulier SSO et SLO  -->
    <SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://idp.test/idp/profile/SAML2/Redirect/SSO"/>
    <SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="https://idp.test/idp/profile/SAML2/Redirect/SLO"/>
    <SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="https://idp.test/idp/profile/SAML2/POST/SLO"/>
    <!-- NameID possibles de la part de cet IDP  -->
    <NameIDFormat>urn:mace:shibboleth:1.0:nameIdentifier</NameIDFormat>
    <NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</NameIDFormat>
  </IDPSSODescriptor>
</EntityDescriptor>
```








<!-- .slide: class="slide" -->
### Une assertion SAML
Les points importants :
```xml
<?xml version="1.0" encoding="UTF-8"?>
  <saml2:Assertion ... IssueInstant="2018-06-07T11:00:48.151Z" ...>
    <saml2:Issuer>https://idp.test/idp/shibboleth</saml2:Issuer>
    <ds:Signature ...><ds:SignedInfo>...infos algo...</ds:SignedInfo><ds:SignatureValue>Signature de l'assertion par l'idp</ds:SignatureValue><ds:KeyInfo>... Rappel certificat IDP ...</ds:KeyInfo>
    </ds:Signature>
    <saml2:Subject>
      <saml2:NameID ... NameQualifier="https://idp.test/idp/shibboleth" SPNameQualifier="https://mon-appli.test" ...>...</saml2:NameID>
      <!-- Infos validité de l'assertion -->
      <saml2:SubjectConfirmation ...>
        <saml2:SubjectConfirmationData Address="10.92.108.2%3" InResponseTo="_0196a897-0111-4d6f-9c69-d35436e7c590" NotOnOrAfter="2018-06-07T11:05:48.163Z" Recipient="https://mon-appli.test/saml/SAMLAssertionConsumer"/>
      </saml2:SubjectConfirmation>
    </saml2:Subject>
    ...
    <saml2:AuthnStatement ...><saml2:SubjectLocality Address="192.168.1.1%3"/><saml2:AuthnContext>
    <!-- Quelle méthode d'authentification a été réalisée -->
        <saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos</saml2:AuthnContextClassRef>
      </saml2:AuthnContext></saml2:AuthnStatement>
    <saml2:AttributeStatement>... Attributs ...</saml2:AttributeStatement>
</saml2:Assertion>
```


