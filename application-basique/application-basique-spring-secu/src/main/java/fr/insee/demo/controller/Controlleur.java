package fr.insee.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controlleur {

  @Value("${spring.security.oauth2.client.provider.kc-formation.issuer-uri}")
  private String keycloakUrl;

  Logger log = LoggerFactory.getLogger(Controlleur.class);

  @RequestMapping(value = { "/accueil", "/" })
  public String accueil(Model model) {
    return "accueil";
  }

  @RequestMapping(value = { "/private", "/admin" })
  public String espacePrivate(HttpServletRequest request, Model model) {

    model.addAttribute("secure", request.isSecure());

    String modeAuthentification = "";
    if (request.getUserPrincipal() == null) {
      modeAuthentification = "Authentifié par ... pas d'authentification en fait";
    } else {
      modeAuthentification = "OpenIDConnect";
    }

    model.addAttribute("typeAuthentification", modeAuthentification);

    KeycloakSecurityContext sc = (KeycloakSecurityContext) request
        .getAttribute(KeycloakSecurityContext.class.getName());

    AccessToken accessToken = sc.getToken();

    model.addAttribute("nom", accessToken.getPreferredUsername());
    model.addAttribute("mail", accessToken.getEmail());
    model.addAttribute("roles", accessToken.getRealmAccess().getRoles());
    model.addAttribute("urlAccount",
        keycloakUrl + "/account" + "?referrer=localhost-web&referrer_uri=" + request.getRequestURL());

    /* Envoi d'une requête au ws */

    HttpURLConnection connection = null;
    try {

      // Create connection
      URL url = new URL(keycloakUrl + "/protocol/openid-connect/userinfo");
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", "Bearer " + sc.getTokenString());

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