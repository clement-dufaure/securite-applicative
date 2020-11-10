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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controlleur {

  @Value("${demo.backend.url}")
  private String demoBackendUrl;

  @RequestMapping("/ressource")
  public void ressource(HttpServletRequest request, HttpServletResponse response, Model model) {
    PrintWriter responseWriter;
    try {
      responseWriter = response.getWriter();

      responseWriter.write("Vous êtes authentifiés en tant que : " + request.getUserPrincipal().getName() + "\n");

      // Basic Authentication
      if (request.getUserPrincipal() instanceof UsernamePasswordAuthenticationToken) {
        responseWriter.write("via une authentification basic \n");
      }

      // Bearer Authentication
      if (request.getUserPrincipal() instanceof JwtAuthenticationToken) {
        responseWriter.write("via une authentification bearer \n");
        JwtAuthenticationToken token = (JwtAuthenticationToken) request.getUserPrincipal();

        responseWriter.write(
            "Les donnees de " + token.getTokenAttributes().get("preferred_username") + " sur /userinfo sont : \n");

        // DEMO appel en cascade à un autre service

        HttpURLConnection connection = null;
        try {
          // Create connection
          URL url = new URL(demoBackendUrl);
          connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");
          connection.setRequestProperty("Authorization", "Bearer " + token.getToken().getTokenValue());

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
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
