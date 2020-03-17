package fr.insee.demo.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class Controlleur {

  Logger log = LoggerFactory.getLogger(Controlleur.class);

  @RequestMapping(value = {"/accueil", "/"})
  public String accueil() {
    return "accueil";
  }

  @RequestMapping(value = {"/private", "/privatetest"})
  public String espacePrivate(HttpServletRequest request, Model model) {

    model.addAttribute("secure", request.isSecure());

    KeycloakSecurityContext sc =
        (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

    String modeAuthentification = "";
    if (request.getUserPrincipal() == null) {
      modeAuthentification = "... pas d'authentification en fait";
    } else {
      modeAuthentification = "OpenIDConnect avec filter Keycloak sur le realm " + sc.getRealm();
    }

    model.addAttribute("typeAuthentification", modeAuthentification);

    AccessToken accessToken = sc.getToken();
    String accessTokenString = sc.getTokenString();

    model.addAttribute("nom", accessToken.getPreferredUsername());
    model.addAttribute("mail", accessToken.getEmail());
    model.addAttribute("roles", accessToken.getRealmAccess().getRoles());

    /* Envoi d'une requête au ws */

    HttpURLConnection connection = null;
    try { // Create connection

      URL url = new URL("https://auth.insee.test/auth/realms/" + sc.getRealm()
          + "/protocol/openid-connect/userinfo");
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", "Bearer " + accessTokenString);
      connection.setDoOutput(true);

      // Send request
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.close();

      // Get Response
      InputStream is = connection.getInputStream();
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
    String realm =
        ((KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName()))
            .getRealm();

    request.getSession().invalidate();
    return new RedirectView("https://auth.insee.test/auth/realms/" + realm
        + "/protocol/openid-connect/logout?redirect_uri=https://localhost:8443/");
  }

  @RequestMapping("/choixRealm")
  public String choixrealm(HttpServletRequest request) {
    return "choixRealm";
  }

}
