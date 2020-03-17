package fr.insee.demo.ws.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controlleur {

  @RequestMapping("/ressource")
  public void ressource(HttpServletRequest request, HttpServletResponse response, Model model) {
    KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
    PrintWriter responseWriter;
    try {
      responseWriter = response.getWriter();
      if (token != null) {
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) token.getDetails();
        AccessToken accessToken = details.getKeycloakSecurityContext().getToken();
        String accessTokenString = details.getKeycloakSecurityContext().getTokenString();
        responseWriter.write("Les donnees de " + accessToken.getPreferredUsername()
            + " sur /userinfo de Keycloak sont : ");

        HttpURLConnection connection = null;
        try {
          // Create connection
          URL url = new URL(
              "https://auth.insee.test/auth/realms/formation-secu-applicative/protocol/openid-connect/userinfo");
          connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");
          connection.setRequestProperty("Authorization", "Bearer " + accessTokenString);

          // Get Response
          InputStream is = connection.getInputStream();
          BufferedReader rd = new BufferedReader(new InputStreamReader(is));
          StringBuffer responseUI = new StringBuffer();
          String line;
          while ((line = rd.readLine()) != null) {
            responseUI.append(line);
            responseUI.append('\r');
          }
          rd.close();
          responseWriter.write(responseUI.toString());
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (connection != null) {
            connection.disconnect();
          }
        }

      } else {
        // Impossible normalement car phase d'authentification imposée
        responseWriter.write("Utilisteur non identifié");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
