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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class Controlleur {

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
      modeAuthentification = "Vous êtes bien authentifié";
    }

    model.addAttribute("typeAuthentification", modeAuthentification);

    /*
     * Pour récupérer des informations sur l'utilisateur avec un adapter keycloak :
     * 
     * KeycloakSecurityContext sc = (KeycloakSecurityContext)
     * request.getAttribute(KeycloakSecurityContext.class.getName());
     * 
     * 
     */

    model.addAttribute("nom", "anonyme");
    model.addAttribute("mail", "");
    model.addAttribute("roles", "");
    model.addAttribute("urlAccount", "https://auth.insee.test/auth/realms/formation-secu-applicative/account"
        + "?referrer=client-test-web&referrer_uri=https://localhost:8443/private");

    /* Envoi d'une requête au ws */

    HttpURLConnection connection = null;
    try {
      // Create connection
      URL url = new URL(
          "https://auth.insee.test/auth/realms/formation-secu-applicative/protocol/openid-connect/userinfo");
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

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

  @RequestMapping("/logout")
  public RedirectView logout(HttpServletRequest request) {
    // TODO
    return new RedirectView("");
  }

}
