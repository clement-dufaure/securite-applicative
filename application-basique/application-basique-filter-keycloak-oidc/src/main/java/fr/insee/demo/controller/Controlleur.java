package fr.insee.demo.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
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

  @RequestMapping(value = {"/private", "/admin"})
  public String espacePrivate(HttpServletRequest request, Model model) {
    
    model.addAttribute("secure", request.isSecure());

    String modeAuthentification = "";
    if (request.getUserPrincipal() == null) {
      modeAuthentification = "... pas d'authentification en fait";
    } else {
      modeAuthentification = "OpenIDConnect avec filter Keycloak";
    }

    model.addAttribute("typeAuthentification", modeAuthentification);


    KeycloakSecurityContext ksc =
        (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

    AccessToken accessToken = ksc.getToken();
    String accessTokenString = ksc.getTokenString();

    model.addAttribute("nom", accessToken.getPreferredUsername());
    model.addAttribute("mail", accessToken.getEmail());
    model.addAttribute("roles", accessToken.getRealmAccess().getRoles());
    model.addAttribute("urlAccount", getAccountUrl(request)
        + "?referrer=client-test-web&referrer_uri=https://localhost:8443/private");

    /* Envoi d'une requête au ws */

    HttpURLConnection connection = null;
    try { // Create connection
      URL url = new URL(getUserInfoUrl(request));
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
  public RedirectView logout(HttpServletRequest request) throws ServletException {
    request.logout();
    request.getSession().invalidate();
    // envoi sur
    // https://auth.insee.test/auth/realms/formation-secu-applicative/protocol/openid-connect/logout?redirect_uri=https://localhost:8443/
    // pour deconnexion sur Keycloak
    //return new RedirectView(getLogoutUrl(request) + "?redirect_uri=https://localhost:8443/");
    return new RedirectView("/");
  }

  private String getLogoutUrl(HttpServletRequest req) {
    return getKeycloakDeployment(req).getLogoutUrl().build(new Object[0]).toString();
  }

  private String getUserInfoUrl(HttpServletRequest req) {
    return getKeycloakDeployment(req).getAuthServerBaseUrl() + "/realms/"
        + getKeycloakDeployment(req).getRealm() + "/protocol/openid-connect/userinfo";
  }

  private String getAccountUrl(HttpServletRequest req) {
    return getKeycloakDeployment(req).getAccountUrl();
  }

  // Récupérer configuration keycloak
  private KeycloakDeployment getKeycloakDeployment(HttpServletRequest req) {
    AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) req.getServletContext()
        .getAttribute(AdapterDeploymentContext.class.getName());
    return deploymentContext.resolveDeployment(null);
  }

}
