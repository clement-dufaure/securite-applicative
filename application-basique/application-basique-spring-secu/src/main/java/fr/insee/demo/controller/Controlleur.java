package fr.insee.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controlleur {

  @Value("${spring.security.oauth2.client.provider.test.issuer-uri}")
  private String keycloakUrl;

  @Autowired
  OAuth2AuthorizedClientService clientService;

  Logger log = LoggerFactory.getLogger(Controlleur.class);

  @RequestMapping(value = { "/accueil", "/" })
  public String accueil(Model model) {
    return "accueil";
  }

  @RequestMapping(value = { "/private", "/admin" })
  public String espacePrivate(HttpServletRequest request, Model model, @AuthenticationPrincipal OidcUser principal) {

    model.addAttribute("secure", request.isSecure());

    String modeAuthentification = "";
    if (request.getUserPrincipal() == null) {
      modeAuthentification = "Authentifié par ... pas d'authentification en fait";
    } else {
      modeAuthentification = "OpenIDConnect";
    }

    model.addAttribute("typeAuthentification", modeAuthentification);

    model.addAttribute("nom", principal.getFullName());
    model.addAttribute("mail", principal.getEmail());
    model.addAttribute("roles", principal.getClaim("groups"));
    model.addAttribute("urlAccount",
        keycloakUrl + "/account" + "?referrer=localhost-web&referrer_uri=" + request.getRequestURL());

    /* Envoi d'une requête au ws */

    HttpURLConnection connection = null;
    try {

      // Get access token
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
      String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
      OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
      String accessToken = client.getAccessToken().getTokenValue();

      // Create connection
      URL url = new URL("http://localhost:8180/auth/realms/test/protocol/openid-connect/userinfo");
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);

      // Get Response
      InputStream is = null;
      try {
        is = connection.getInputStream();
      } catch (IOException e) {
        is = connection.getErrorStream();
      }

      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      model.addAttribute("reponseWS", response.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    return "private";
  }

}