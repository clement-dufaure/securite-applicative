package fr.insee.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Controlleur {

  Logger log = LoggerFactory.getLogger(Controlleur.class);

  private String webServiceUrl =
      "http://localhost:8180/auth/realms/test/protocol" + "/openid-connect/userinfo";

  @Operation
  @RequestMapping(
      value = {"/", "healthcheck"},
      method = RequestMethod.GET)
  public void accueil(HttpServletResponse response) throws IOException {
    PrintWriter responseWriter = response.getWriter();
    responseWriter.println("Page sans authentification");
    responseWriter.println("Tout va bien");
  }

  @Operation
  @RequestMapping(
      value = {"/private", "/admin"},
      method = RequestMethod.GET)
  public void espacePrivate(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    PrintWriter responseWriter = response.getWriter();

    responseWriter.write("requete en https : " + request.isSecure() + "\n");

    if (request.getUserPrincipal() == null) {
      responseWriter.println("Authentifié par ... pas d'authentification en fait");
    } else {
      // Quelquesoit le mode d'authentification
      responseWriter.println("Vous etes : " + request.getUserPrincipal().getName());
      responseWriter.println("Vous êtes admin : " + request.isUserInRole("admin"));
    }

    // Comportements selon le mode d'authentification
    if (request.getUserPrincipal() instanceof UsernamePasswordAuthenticationToken) {
      responseWriter.write("Authentification Basic");
    }

    if (request.getUserPrincipal() instanceof JwtAuthenticationToken token) {

      responseWriter.println("mail" + token.getTokenAttributes().get("mail"));
      responseWriter.println("roles" + token.getAuthorities());

      /* Envoi d'une requête au ws */

      OkHttpClient client = new OkHttpClient();

      Request req =
          new Request.Builder()
              .url(webServiceUrl)
              .header("Authorization", "Bearer " + token.getToken().getTokenValue())
              .get()
              .build();

      Response rep = client.newCall(req).execute();
      responseWriter.println(rep.body().string());
    }
  }
}
